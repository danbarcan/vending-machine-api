package payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductResponse {
    private String productName;
    private long cost;
    private int amountPurchased;
}
