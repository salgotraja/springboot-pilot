package io.js.app.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    @SequenceGenerator(name = "user_id_generator", sequenceName = "user_id_seq")
    private Long id;

    @Column(nullable = false)
    @NotEmpty()
    private String name;

    @Column(nullable = false, unique = true)
    @NotEmpty
    @Email(message = "Invalid email")
    private String email;

    @Column(nullable = false)
    @NotEmpty
    @Size(min = 4)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
}
