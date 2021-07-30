package payload;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.User;

@Data
@NoArgsConstructor
public class UserUpdateRequest {
    private String password;
}
