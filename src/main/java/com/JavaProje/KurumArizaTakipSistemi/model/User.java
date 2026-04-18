package com.JavaProje.KurumArizaTakipSistemi.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "is_verified")
    private Boolean Verified = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}