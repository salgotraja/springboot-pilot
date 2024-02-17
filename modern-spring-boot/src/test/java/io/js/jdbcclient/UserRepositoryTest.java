package io.js.jdbcclient;

import io.js.ContainersConfig;
import io.js.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@JdbcTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(ContainersConfig.class)
class UserRepositoryTest {

    @Autowired
    JdbcClient jdbcClient;

    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository(jdbcClient);
    }

    @Test
    void shouldCreateUser() {
        User user = new User(null, "Jagdish", "salgotraja", "salgotraja@gmail.com", "99999999", "salgotraja.in");
        Long id = userRepository.save(user);
        assertThat(id).isNotNull();
    }

    @Test
    void shouldNotSaveUserWithExistingUsernameOrEmail() {
        User existingUser = new User(null, "Existing User", "existinguser", "existinguser@gmail.com", "1111111111", "existinguser.in");
        Long existingId = userRepository.save(existingUser);
        assertThat(existingId).isNotNull();

        User newUser = new User(null, "New User", "existinguser", "newuser@gmail.com", "2222222222", "newuser.in");
        Long newId = userRepository.save(newUser);

        assertThat(newId).isNull();
    }

    @Test
    void shouldThrowExceptionOnSavingUserWithMissingRequiredAttributes() {
        User user = new User(null, "Jagdish", null, null, null, null);

        Throwable thrown = catchThrowable(() -> userRepository.save(user));
        assertThat(thrown).isInstanceOf(DataIntegrityViolationException.class);
    }
}