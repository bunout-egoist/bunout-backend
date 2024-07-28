package dough.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    INVALID_REQUEST(1000, "올바르지 않은 요청입니다."),

    // 2000 quest
    INVALID_QUEST_TYPE(2000, "유효하지 않은 퀘스트 타입입니다."),
    NOT_FOUND_QUEST_ID(2001, "요청하신 ID에 해당하는 퀘스트를 찾을 수 없습니다."),

    // 3000 member

    NOT_FOUND_MEMBER_ID(3000, "요청하신 ID에 해당하는 유저를 찾을 수 없습니다."),

    // 4000 feedback

    INTERNAL_SEVER_ERROR(9999,"서버에서 에러가 발생하였습니다.");
    
    private final int code;
    private final String message;
}
