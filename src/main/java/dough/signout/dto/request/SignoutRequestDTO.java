package dough.signout.dto.request;

import lombok.Getter;

@Getter
public class SignoutRequestDTO {
    private final String token;

    public SignoutRequestDTO(String token) {
        this.token = token;
    }
}
