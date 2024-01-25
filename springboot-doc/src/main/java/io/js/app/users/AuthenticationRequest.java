package io.js.app.users;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationRequest {
    @NotBlank(message = "UserName cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
