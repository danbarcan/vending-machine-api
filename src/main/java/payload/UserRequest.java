package payload;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.User;

@Data
@NoArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private User.Role role;

    public boolean isValid() {
        return username != null && !username.isBlank() && password != null && !password.isBlank() && role != null;
    }
}
