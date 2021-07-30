package service;

import com.j256.ormlite.dao.Dao;
import dao.DaoFactory;
import model.Product;
import model.User;
import payload.BuyRequest;
import payload.BuyResponse;
import payload.ProductRequest;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductService {
    private Dao<Product, Long> productDao;
    private UserService userService;

    public ProductService() {
        try {
            this.productDao = DaoFactory.getProductDao();
        } catch (SQLException e) {
            e.printStackTrace();//todo
        }
        this.userService = new UserService();
    }

    public List<Product> findAll() {
        try {
            return productDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Product findProductById(Long id) {
        try {
            return productDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Product createProduct(ProductRequest productRequest, String username) {
        User seller = userService.findUserByUsername(username);
        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .cost(productRequest.getCost())
                .amountAvailable(productRequest.getAmountAvailable())
                .sellerId(seller.getId())
                .build();

        try {
            return productDao.createIfNotExists(product);
        } catch (SQLException e) {
            e.printStackTrace();//todo
            return null;
        }
    }

    public int updateProduct(Long userId, ProductRequest productRequest) {
        try {
            Product existingProduct = productDao.queryForId(userId);
            if (existingProduct == null) {
                return -1;
            }
            existingProduct.setCost(productRequest.getCost());
            existingProduct.setAmountAvailable(productRequest.getAmountAvailable());
            existingProduct.setProductName(productRequest.getProductName());
            return productDao.update(existingProduct);
        } catch (SQLException e) {
            e.printStackTrace();//todo
            return -2;
        }
    }

    public int deleteProductById(Long id) {
        try {
            return productDao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public BuyResponse buy(BuyRequest buyRequest, String username) {
        User user = userService.findUserByUsername(username);
        List<Product> products = buyRequest.getProductIds().stream().map(this::findProductById).collect(Collectors.toList());

        long totalCost = products.stream().mapToLong(Product::getCost).sum();
        if (totalCost > user.getDeposit()) {
            return null; // todo throw exception
        } else {
            long change = user.getDeposit() - totalCost;
            user.setDeposit(change);
            products.forEach(product -> {
                product.setAmountAvailable(product.getAmountAvailable() - 1);
            });
            if (products.stream().anyMatch(product -> product.getAmountAvailable() < 0)) {
                //todo throw exception
                return null;
            }

            try {
                products.forEach(product -> {
                    try {
                        productDao.update(product);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                userService.updateUser(user);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            return BuyResponse.builder().change(getChange(change)).productsPurchased(products).totalSpent(totalCost).build();
        }
    }

    private Map<Integer, Long> getChange(long sum) {
        List<Integer> coins = List.of(100, 50, 20, 10, 5);
        Map<Integer, Long> changeReturn = new LinkedHashMap<>();
        for(Integer coin : coins) {

            long result = sum / coin;
            sum = sum % coin;
            if (result > 0) {
                changeReturn.put(coin, result);
            }
        }
        return changeReturn;
    }
}
