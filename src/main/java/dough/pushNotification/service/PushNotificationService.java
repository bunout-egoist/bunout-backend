package dough.pushNotification.service;

import com.google.firebase.messaging.*;
import dough.global.exception.BadRequestException;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dough.global.exception.ExceptionCode.FAIL_TO_FCM_REQUEST;

@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final MemberRepository memberRepository;
    private static final int BATCH_SIZE = 500;

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyQuest() {
        sendNotification("%s님, 오늘의 퀘스트가 도착했어요!", null, null, null);
    }

    @Scheduled(cron = "0 0 18 * * ?")
    public void sendLeftQuest() {
        sendNotification("잠깐! 🤚 %s님, 집에 가기 전에 번아웃 잊지 않으셨죠?!",
                null, null, null);
    }

    @Scheduled(cron = "0 0 13 * * 1,3,7")
    public void sendSpecialQuest() {
        sendNotification("[%s], 지금 뭐해?", "스페셜 퀘스트가 도착했어요!", null, null);
    }

    private void sendNotification(String titleTemplate, String bodyTemplate, String clickAction, Map<String, String> customData) {
        List<Member> members = memberRepository.findAll();

        List<String> tokens = getValidTokens(members);

        IntStream.range(0, calculateNumberOfBatches(tokens.size())).forEach(i -> {
            List<String> batchTokens = getBatchTokens(tokens, i);

            List<Message> messages = batchTokens.stream()
                    .map(token -> buildMessageForMember(members.get(tokens.indexOf(token)), token, titleTemplate, bodyTemplate, clickAction, customData))
                    .collect(Collectors.toList());

            sendBatchMessages(messages);
        });
    }

    private List<String> getValidTokens(List<Member> members) {
        return members.stream()
                .map(Member::getNotificationToken)
                .filter(token -> token != null && !token.isEmpty())
                .collect(Collectors.toList());
    }

    private int calculateNumberOfBatches(int totalSize) {
        return (totalSize + BATCH_SIZE - 1) / BATCH_SIZE;
    }

    private List<String> getBatchTokens(List<String> tokens, int batchIndex) {
        int start = batchIndex * BATCH_SIZE;
        int end = Math.min(start + BATCH_SIZE, tokens.size());
        return tokens.subList(start, end);
    }

    private Message buildMessageForMember(Member member, String token, String titleTemplate, String bodyTemplate, String clickAction, Map<String, String> customData) {
        String nickname = member.getNickname();
        String title = String.format(titleTemplate, nickname);
        String body = bodyTemplate != null ? String.format(bodyTemplate, nickname) : null;

        return Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .setClickAction(clickAction)
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .putCustomData("alert", Map.of(
                                        "title", title,
                                        "body", body
                                ))
                                .putCustomData("sound", "default")
                                .build())
                        .build())
                .build();
    }

    private void sendBatchMessages(List<Message> messages) {
        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendAll(messages);
            System.out.println(response.getSuccessCount() + " messages were sent successfully");
        } catch (Exception e) {
            throw new BadRequestException(FAIL_TO_FCM_REQUEST);
        }
    }
}
