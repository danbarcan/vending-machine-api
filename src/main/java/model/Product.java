package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
