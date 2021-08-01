package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "products")
public class Product {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField(unique = true)
    private String productName;
    @DatabaseField
    private long cost;
    @DatabaseField
    private int amountAvailable;
    @DatabaseField
    private long sellerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (id != product.id) return false;
        if (cost != product.cost) return false;
        if (sellerId != product.sellerId) return false;
        return Objects.equals(productName, product.productName);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (productName != null ? productName.hashCode() : 0);
        result = 31 * result + (int) (cost ^ (cost >>> 32));
        result = 31 * result + (int) (sellerId ^ (sellerId >>> 32));
        return result;
    }
}
