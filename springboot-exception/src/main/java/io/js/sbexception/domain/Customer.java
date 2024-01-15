package io.js.sbexception.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Table(name = "customers")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "Name can not be empty")
    private String name;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Email can not be empty")
    @Email(message = "Email is invalid")
    private String email;
}
