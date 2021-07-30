package payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Product;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyResponse {
    private long totalSpent;
    List<Product> productsPurchased;
    private Map<Integer, Long> change;
}
