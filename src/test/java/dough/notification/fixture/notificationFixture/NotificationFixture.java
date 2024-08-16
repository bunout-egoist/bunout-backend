package dough.notification.fixture.notificationFixture;

import dough.notification.domain.Notification;

import static dough.member.fixture.MemberFixture.MEMBER;
import static dough.notification.domain.type.NotificationType.*;

public class NotificationFixture {

    public static final Notification DAILY_NOTIFICATION = new Notification(
            1L,
            MEMBER,
            DAILY_QUEST
    );

    public static final Notification SPECIAL_NOTIFICATION = new Notification(
            2L,
            MEMBER,
            SPECIAL_QUEST
    );

    public static final Notification REMAINING_NOTIFICATION = new Notification(
            3L,
            MEMBER,
            REMAINING_QUEST
    );
}
