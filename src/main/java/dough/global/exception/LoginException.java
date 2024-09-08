package dough.global.exception;

import lombok.Getter;

@Getter
public class LoginException extends BadRequestException {

    public LoginException(final ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}