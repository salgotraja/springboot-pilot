package io.js.app.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
    @NotBlank(message = "UserName cannot be blank")
    @Size(min = 5, max = 30, message = "Username must be between 5 and 30 characters")
    private String username;

    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
