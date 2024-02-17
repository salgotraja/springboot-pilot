package io.js.httpclient;

import io.js.ContainersConfig;
import io.js.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(ContainersConfig.class)
class JsonPlaceHolderHttpClientTest {

    @Autowired
    JsonPlaceHolderHttpClient client;

    @Test
    void findAll() {
        List<User> users = client.findAllUsers();
        assertThat(users).isNotEmpty();
        assertThat(users).isNotNull();
    }

    @Test
    void findUserById() {
        User user = client.findUserById(1);
        assertThat(user).isNotNull();
    }

    @Test
    void createUser() {
        User user = new User(11, "testuser", "testuser", "testuser@gmail.com", "1234567890", "https://testuser.com");
        User createdUser = client.createUser(user);
        assertThat(createdUser).isNotNull();
    }

    @Test
    void deleteUser() {
        int userId = 11;
        client.deleteUser(userId);

        assertThatThrownBy(() -> client.findUserById(userId)).isInstanceOf(HttpClientErrorException.NotFound.class);
    }
}