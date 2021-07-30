package payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class DepositRequest {
    private long fives;
    private long tens;
    private long twenties;
    private long fifties;
    private long hundreds;

    public Map<Integer, Long> toMap() {
        return Map.of(5, fives, 10, tens, 20, twenties, 50, fifties, 100, hundreds);
    }
}
