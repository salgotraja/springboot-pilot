package io.js.app.users.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
public class PasswordGenerator {
    private final BCryptPasswordEncoder encoder;

    public static void main(String[] args) {
        var password = new PasswordGenerator(new BCryptPasswordEncoder()).generatePassword("user");
        System.out.println(password);
    }

    private String generatePassword(String key) {
        return encoder.encode(key);
    }
}
