package payload;

import exception.InvalidRequestBodyException;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductRequest {
    private String productName;
    private long cost;
    private int amountAvailable;

    public boolean isValid() throws InvalidRequestBodyException {
        if (productName != null && !productName.isBlank() && cost > 0) {
            return true;
        }

        throw new InvalidRequestBodyException();
    }
}
