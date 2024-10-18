package dough.pushNotification.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.*;
import com.google.storage.v2.NotificationConfigOrBuilder;
import dough.global.exception.BadRequestException;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dough.global.exception.ExceptionCode.FAIL_TO_FCM_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
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

        List<Message> messages = new LinkedList<>();

        for(int i=0; i<members.size(); i++) {
            if(members.get(i).getNotificationToken() != null) {
                messages.add(buildMessageForMember(members.get(i), members.get(i).getNotificationToken(), titleTemplate, bodyTemplate));
            }
        }

        sendBatchMessages(messages);
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

    private Message buildMessageForMember(Member member, String token, String titleTemplate, String bodyTemplate) {
        String nickname = member.getNickname();
        String title = String.format(titleTemplate, nickname);
        String body = bodyTemplate != null ? String.format(bodyTemplate, nickname) : null;

        return Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .setImage("https://bunout-bucket.s3.ap-northeast-2.amazonaws.com/image.jpg")
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600 * 1000)
                        .setNotification(AndroidNotification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .setIcon("https://bunout-bucket.s3.ap-northeast-2.amazonaws.com/image.jpg")
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setBadge(42)
                                .build())
                        .build())
                .setWebpushConfig(WebpushConfig.builder()
                        .putHeader("ttl", "300")
                        .putData("icon", "https://bunout-bucket.s3.ap-northeast-2.amazonaws.com/image.jpg")
                        .build())
                .build();
    }

    private void sendBatchMessages(List<Message> messages) {
        try {
            // Send messages asynchronously
            ApiFuture<BatchResponse> futureResponse = FirebaseMessaging.getInstance().sendEachAsync(messages);

            // Wait for the single result
            BatchResponse response = futureResponse.get(); // This may throw an InterruptedException or ExecutionException

            // Process success count directly from the single response
            log.info("{} messages were sent successfully", response.getSuccessCount());

        } catch (Exception e) {
            log.error("Failed to send batch messages", e);
            throw new BadRequestException(FAIL_TO_FCM_REQUEST);
        }
    }


}
