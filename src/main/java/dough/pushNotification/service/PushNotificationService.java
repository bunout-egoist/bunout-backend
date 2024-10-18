package dough.pushNotification.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.*;
import dough.global.exception.BadRequestException;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

import static dough.global.exception.ExceptionCode.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PushNotificationService {

    private final MemberRepository memberRepository;
//    private static final int BATCH_SIZE = 500;

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyQuest() {
        try {
            sendNotification("%s님, 오늘의 퀘스트가 도착했어요!", null);
        }
        catch (Exception e) {
            throw new BadRequestException(FAIL_TO_REQUEST_DAILY_PUSH_REQUEST);
        }
    }

    @Scheduled(cron = "0 0 18 * * ?")
    public void sendLeftQuest() {
        try {
            sendNotification("잠깐! 🤚 %s님, 집에 가기 전에 번아웃 잊지 않으셨죠?!",
                    null);
        }
        catch (Exception e) {
            throw new BadRequestException(FAIL_TO_REQUEST_LEFT_PUSH_REQUEST);
        }
    }

    @Scheduled(cron = "0 0 13 * * 1,3,7")
    public void sendSpecialQuest() {
        try {
            sendNotification("[%s], 지금 뭐해?", "스페셜 퀘스트가 도착했어요!");
        }
        catch (Exception e) {
            throw new BadRequestException(FAIL_TO_REQUEST_SPECIAL_PUSH_REQUEST);
        }
    }

    private void sendNotification(String titleTemplate, String bodyTemplate) {
        List<Member> members = memberRepository.findAll();

        List<Message> messages = new LinkedList<>();

        for(int i=0; i<members.size(); i++) {
            if(members.get(i).getNotificationToken() != null) {
                messages.add(buildMessageForMember(members.get(i), members.get(i).getNotificationToken(), titleTemplate, bodyTemplate));
            }
        }

        sendBatchMessages(messages);
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
            ApiFuture<BatchResponse> futureResponse = FirebaseMessaging.getInstance().sendEachAsync(messages);

            BatchResponse response = futureResponse.get(); // This may throw an InterruptedException or ExecutionException

            log.info("{} messages were sent successfully", response.getSuccessCount());

        } catch (Exception e) {
            log.error("Failed to send batch messages", e);
            throw new BadRequestException(FAIL_TO_FCM_REQUEST);
        }
    }


}
