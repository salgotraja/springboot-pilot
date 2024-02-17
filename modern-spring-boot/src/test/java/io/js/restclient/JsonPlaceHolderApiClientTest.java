package io.js.restclient;

import io.js.AbstractIntegrationTest;
import io.js.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonPlaceHolderApiClientTest extends AbstractIntegrationTest {

    @Autowired
    private JsonPlaceHolderApiClient jsonPlaceHolderApiClient;

    @Test
    void shouldGetAllUsers() {
        List<User> users = jsonPlaceHolderApiClient.getAllUsers();

        assertThat(users).isNotNull();
        assertThat(users).isNotEmpty();
    }

    @Test
    void shouldGetUserById() {
        User user = jsonPlaceHolderApiClient.getUserById(1);

        assertThat(user).isNotNull();
        assertThat(user.id()).isEqualTo(1);
        assertThat(user.name()).isEqualTo("Leanne Graham");
        assertThat(user.username()).isEqualTo("Bret");
        assertThat(user.email()).isEqualTo("Sincere@april.biz");
        assertThat(user.phone()).isEqualTo("1-770-736-8031 x56442");
        assertThat(user.website()).isEqualTo("hildegard.org");
    }

    @Test
    void shouldCreateUser() {
        User user = new User(null, "John Doe", "johndoe", "johndoe@example.com", "1234567890", "johndoe.com");

        User createdUser = jsonPlaceHolderApiClient.createUser(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.id()).isEqualTo(11);
        assertThat(createdUser.name()).isEqualTo("John Doe");
        assertThat(createdUser.username()).isEqualTo("johndoe");
        assertThat(createdUser.email()).isEqualTo("johndoe@example.com");
        assertThat(createdUser.phone()).isEqualTo("1234567890");
        assertThat(createdUser.website()).isEqualTo("johndoe.com");
    }

    @Test
    void shouldDeleteUser() {
        User user = new User(null, "John Doe", "johndoe", "johndoe@example.com", "1234567890", "johndoe.com");

        User createdUser = jsonPlaceHolderApiClient.createUser(user);
        jsonPlaceHolderApiClient.deleteUser(createdUser.id());

        assertThatThrownBy(() -> jsonPlaceHolderApiClient.getUserById(createdUser.id())).isInstanceOf(HttpClientErrorException.NotFound.class);
    }
}