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
    NOT_FOUND_SELECTED_QUEST_ID(2001, "요청하신 ID에 해당하는 선택된 퀘스트를 찾을 수 없습니다."),

    // 3000 member

    NOT_FOUND_MEMBER_ID(3000, "요청하신 ID에 해당하는 유저를 찾을 수 없습니다."),
    ALREADY_UPDATED_BURNOUT_TYPE(3001, "번아웃 유형은 이번 달에 이미 수정되었습니다."),
    ALREADY_UPDATED_FIXED_QUEST(3002, "고정 퀘스트는 이번 주에 재설정되었습니다."),

    // 4000 pushNotification
    FAIL_TO_FCM_REQUEST(4000, "FCM 요청에 실패했습니다."),
    FAIL_TO_REQUEST_DAILY_PUSH_REQUEST(4001, "DAILY QUEST 알림 요청에 실패했습니다"),
    FAIL_TO_REQUEST_LEFT_PUSH_REQUEST(4002, "LEFT 알림 요청에 실패했습니다"),
    FAIL_TO_REQUEST_SPECIAL_PUSH_REQUEST(4003, "SPECIAL QUEST 알림 요청에 실패했습니다"),

    // 5000 burnout
    NOT_FOUND_BURNOUT_ID(5000, "요청하신 ID에 해당하는 번아웃 유형을 찾을 수 없습니다."),

    // 6000 notification
    NOT_FOUND_NOTIFICATION_ID(6000, "요청하신 ID 중 찾을 수 없는 알림이 있습니다."),

    // 7000 keyword
    NOT_FOUND_KEYWORD_ID(7000, "요청하신 키워드를 찾을 수 없습니다."),
    INVALID_PARTICIPATION_TYPE(7001, "유효하지 않은 타입입니다."),
    INVALID_PLACE_TYPE(7001, "유효하지 않은 누구와 타입입니다."),

    // 8000 level
    NOT_FOUND_LEVEL_ID(8000, "요청하신 ID에 해당하는 레벨을 찾을 수 없습니다."),

    INVALID_TOKEN(9000, "잘못된 엑세스 토큰입니다."),
    UNSUPPORTED_TOKEN(9001, "지원하지 않는 형식의 토큰입니다."),
    MALFORMED_TOKEN(9002, "유효하지 않은 구성의 토큰입니다."),
    EXPIRED_TOKEN(9003, "만료된 토큰입니다."),
    FAIL_TO_GET_PUBLIC_KEY(9004, "공개키 얻기를 실패했습니다."),
    FAIL_TO_APPLE_LOGIN(9005, "애플 로그인에 실패했습니다."),
    FAIL_SOCIAL_LOGIN(9006, "소셜 로그인에 실패했습니다."),
    INVALID_REFRESH_TOKEN(9007, "유효하지 않은 RefreshToken입니다."),
    INVALID_ACCESS_TOKEN(9008, "유효하지 않은 AccessToken입니다."),
    EXPIRED_REFRESH_TOKEN(9010, "만료된 AccessToken입니다."),
    EXPIRED_ACCESS_TOKEN(9011, "만료된 RefreshToken입니다."),
    FAIL_TO_RENEW_ACCESS_TOKEN(9012, "액세스 토큰 갱신에 실패했습니다."),


    INTERNAL_SEVER_ERROR(9999, "서버에서 에러가 발생하였습니다.");

    private final int code;
    private final String message;
}
