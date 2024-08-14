package dough.global.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends BadRequestException {
    public UserNotFoundException() {
        super(ExceptionCode.NOT_FOUND_MEMBER_ID);
    }
}