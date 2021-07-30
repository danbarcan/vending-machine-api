package payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductRequest {
    private String productName;
    private long cost;
    private int amountAvailable;

    public boolean isValid() {
        return productName != null && !productName.isBlank() && cost > 0;
    }
}
