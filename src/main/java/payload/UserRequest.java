package payload;

import exception.InvalidRequestBodyException;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.User;

@Data
@NoArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private User.Role role;

    public boolean isValid() throws InvalidRequestBodyException {
        if (username != null && !username.isBlank() && password != null && !password.isBlank() && role != null) {
            return true;
        }
         throw new InvalidRequestBodyException();
    }
}
