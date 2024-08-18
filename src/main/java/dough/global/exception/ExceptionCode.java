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
    ALREADY_USED_QUEST_ID(2002, "요청하신 퀘스트 ID를 사용하고 있는 멤버가 있습니다."),

    // 3000 member

    NOT_FOUND_MEMBER_ID(3000, "요청하신 ID에 해당하는 유저를 찾을 수 없습니다."),
    ALREADY_UPDATED_BURNOUT_TYPE(3001, "번아웃 유형은 이번 달에 이미 수정되었습니다."),
    ALREADY_UPDATED_FIXED_QUEST(3002, "고정 퀘스트는 이번 주에 재설정되었습니다."),

    // 4000 feedback


    // 5000 burnout
    NOT_FOUND_BURNOUT_ID(5000, "요청하신 ID에 해당하는 번아웃 유형을 찾을 수 없습니다."),

    // 6000 notification
    NOT_FOUND_NOTIFICATION_ID(6000, "요청하신 ID 중 찾을 수 없는 알림이 있습니다."),

    // 7000 keyword
    NOT_FOUND_KEYWORD_ID(7000, "요청하신 키워드를 찾을 수 없습니다."),

    INTERNAL_SEVER_ERROR(9999,"서버에서 에러가 발생하였습니다.");

    private final int code;
    private final String message;
}
