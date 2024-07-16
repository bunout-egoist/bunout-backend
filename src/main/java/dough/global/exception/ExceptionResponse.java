package dough.global.exception;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExceptionResponse {

    private int code;
    private String message;
}