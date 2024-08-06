package dough.login.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "social_login_id", nullable = false, unique = true)
    private String socialLoginId;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    public RefreshToken(String socialLoginId, String refreshToken) {
        this.socialLoginId = socialLoginId;
        this.refreshToken = refreshToken;
    }

    public RefreshToken update(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        return this;
    }
}