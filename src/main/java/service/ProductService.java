package service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import dao.DaoFactory;
import exception.*;
import model.Product;
import model.User;
import payload.BuyRequest;
import payload.BuyResponse;
import payload.ProductRequest;
import utils.DatabaseUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductService {
    private Dao<Product, Long> productDao;
    private UserService userService;

    public ProductService() throws SQLException {
        this.productDao = DaoFactory.getProductDao();
        this.userService = new UserService();
    }

    public List<Product> findAll() throws ResourceNotCreatedException {
        try {
            List<Product> products = productDao.queryForAll();
            if (products == null || products.isEmpty()) {
                throw new ResourceNotCreatedException("No products found!");
            }
            return products;
        } catch (SQLException e) {
            throw new ResourceNotCreatedException("No products found!", e);
        }
    }

    public Product findProductById(Long id) throws ResourceNotFoundException {
        Product product = null;
        try {
            product = productDao.queryForId(id);
            if (product == null) {
                throw new ResourceNotFoundException("Product not found!");
            }
            return product;
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Product not found!", e);
        }
    }

    public Product createProduct(ProductRequest productRequest, String username) throws ResourceNotCreatedException, ResourceNotFoundException {
        User seller = userService.findUserByUsername(username);
        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .cost(productRequest.getCost())
                .amountAvailable(productRequest.getAmountAvailable())
                .sellerId(seller.getId())
                .build();

        try {
            product = productDao.createIfNotExists(product);
            if (product == null) {
                throw new ResourceNotCreatedException("Product not created!");
            }
        } catch (SQLException e) {
            throw new ResourceNotCreatedException("Product not created!", e);
        }

        return product;
    }

    public int updateProduct(Long userId, ProductRequest productRequest) throws BaseException {
        try {
            Product existingProduct = productDao.queryForId(userId);
            if (existingProduct == null) {
                throw new ResourceNotFoundException("Product not found");
            }
            existingProduct.setCost(productRequest.getCost());
            existingProduct.setAmountAvailable(productRequest.getAmountAvailable());
            existingProduct.setProductName(productRequest.getProductName());
            return productDao.update(existingProduct);
        } catch (SQLException e) {
            throw new ResourceNotUpdatedException("Product was not created", e);
        }
    }

    public int deleteProductById(Long id) throws ResourceNotDeletedException {
        try {
            return productDao.deleteById(id);
        } catch (SQLException e) {
            throw new ResourceNotDeletedException("Product was not deleted", e);
        }
    }

    public BuyResponse buy(BuyRequest buyRequest, String username) throws BaseException {
        User user = userService.findUserByUsername(username);
        List<Product> products = new ArrayList<>();
        for (Long aLong : buyRequest.getProductIds()) {
            Product productById = findProductById(aLong);
            products.add(productById);
        }

        long totalCost = products.stream().mapToLong(Product::getCost).sum();
        if (totalCost > user.getDeposit()) {
            throw new InsufficientFundsException();
        } else {
            long change = user.getDeposit() - totalCost;
            user.setDeposit(change);
            products.forEach(product -> product.setAmountAvailable(product.getAmountAvailable() - 1));
            if (products.stream().anyMatch(product -> product.getAmountAvailable() < 0)) {
                throw new InsufficientAmountException();
            }

            try {
                TransactionManager.callInTransaction(DatabaseUtils.getDataSourceConnection(), () -> {
                    for (Product product : products) {
                        try {
                            productDao.update(product);
                        } catch (SQLException e) {
                            throw new ResourceNotUpdatedException("Buy could not be completed.", e);
                        }
                    }
                    userService.updateUser(user);

                    return 0;
                });
            } catch (SQLException e) {
                throw new ResourceNotUpdatedException("Buy could not be completed.", e);
            }

            return BuyResponse.builder().change(getChange(change)).productsPurchased(products).totalSpent(totalCost).build();
        }
    }

    private Map<Integer, Long> getChange(long sum) {
        List<Integer> coins = List.of(100, 50, 20, 10, 5);
        Map<Integer, Long> changeReturn = new LinkedHashMap<>();
        for (Integer coin : coins) {

            long result = sum / coin;
            sum = sum % coin;
            if (result > 0) {
                changeReturn.put(coin, result);
            }
        }
        return changeReturn;
    }
}
