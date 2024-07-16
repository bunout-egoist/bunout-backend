package dough.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    INVALID_REQUEST(1000, "올바르지 않은 요청입니다."),

    // 1000 quest

    // 2000 feedback

    // 3000 quest

    SERVER_ERROR(9999,"서버에서 에러가 발생하였습니다.");
    
    private final int code;
    private final String message;
}