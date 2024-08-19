package dough.signout.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class SignoutRequestDTO {
    private String token;

    public SignoutRequestDTO(String token) {
        this.token = token;
    }
}
