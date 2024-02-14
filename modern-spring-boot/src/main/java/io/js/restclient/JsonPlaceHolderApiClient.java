package io.js.restclient;

import io.js.models.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class JsonPlaceHolderApiClient {
    private final RestClient restClient;

    public JsonPlaceHolderApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<User> getAllUsers() {
        return restClient.get()
                .uri("/users")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public User getUserById(int id) {
        return restClient.get()
                .uri("/users/{id}", id)
                .accept(APPLICATION_JSON)
                .retrieve()
                .body(User.class);
    }

    User createUser(User user) {
        return restClient.post()
                .uri("/users")
                .contentType(APPLICATION_JSON)
                .body(user)
                .retrieve()
                .body(User.class);
    }

    void deleteUser(Integer id) {
        restClient.delete()
                .uri("/users/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
