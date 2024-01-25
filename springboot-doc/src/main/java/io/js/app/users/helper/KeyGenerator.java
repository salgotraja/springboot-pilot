package io.js.app.users.helper;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] secretKeyBytes = new byte[64]; // 64 bytes for HmacSHA512
        random.nextBytes(secretKeyBytes);
        String encodedKey = Base64.getEncoder().encodeToString(secretKeyBytes);
        System.out.println("Generated Secret Key: " + encodedKey);
    }
}
