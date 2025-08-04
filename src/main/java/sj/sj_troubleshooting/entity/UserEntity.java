package sj.sj_troubleshooting.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "pass", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;
}
