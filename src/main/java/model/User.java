package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import payload.UserResponse;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "users")
public class User implements Serializable {
    public enum Role {
        BUYER, SELLER
    }

    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField(unique = true)
    private String username;
    @DatabaseField
    private String password;
    @DatabaseField
    private Role role;
    @DatabaseField
    private long deposit;

    public UserResponse toUserResponse() {
        return UserResponse.builder().id(id).username(username).role(role).deposit(deposit).build();
    }
}

