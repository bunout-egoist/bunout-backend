package dough.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.AbstractControllerTest;
import dough.notification.dto.request.NotificationUpdateRequest;
import dough.notification.dto.request.NotificationsUpdateRequest;
import dough.notification.dto.response.NotificationResponse;
import dough.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static dough.global.restdocs.RestDocsConfiguration.field;
import static dough.notification.fixture.notificationFixture.NotificationFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class NotificationControllerTest extends AbstractControllerTest {

    private static final String MEMBER_TOKENS = "accessToken";

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        when(tokenProvider.validToken(any()))
                .thenReturn(true);
        given(tokenProvider.getMemberIdFromToken(any()))
                .willReturn(1L);
    }

    @DisplayName("멤버의 전체 알림을 조회할 수 있다.")
    @Test
    void getAllNotifications() throws Exception {
        // given
        final List<NotificationResponse> notificationResponses = List.of(
                NotificationResponse.of(BY_TYPE_NOTIFICATION),
                NotificationResponse.of(SPECIAL_NOTIFICATION),
                NotificationResponse.of(REMAINING_NOTIFICATION)
        );

        when(notificationService.getAllNotifications())
                .thenReturn(notificationResponses);

        // when
        final ResultActions resultActions = mockMvc.perform(get("/api/v1/notifications")
                .header(AUTHORIZATION, MEMBER_TOKENS));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("[0].id")
                                        .type(NUMBER)
                                        .description("알림 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[0].notificationType")
                                        .type(STRING)
                                        .description("알림 종류")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[0].isChecked")
                                        .type(BOOLEAN)
                                        .description("알림 여부")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("[1].id")
                                        .type(NUMBER)
                                        .description("알림 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[1].notificationType")
                                        .type(STRING)
                                        .description("알림 종류")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[1].isChecked")
                                        .type(BOOLEAN)
                                        .description("알림 여부")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("[2].id")
                                        .type(NUMBER)
                                        .description("알림 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[2].notificationType")
                                        .type(STRING)
                                        .description("알림 종류")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[2].isChecked")
                                        .type(BOOLEAN)
                                        .description("알림 여부")
                                        .attributes(field("constraint", "불리언"))
                        )
                ));
    }

    @DisplayName("알람을 업데이트 할 수 있다.")
    @Test
    void updateNotifications() throws Exception {
        // given
        final NotificationsUpdateRequest notificationsUpdateRequest = new NotificationsUpdateRequest(List.of(
                new NotificationUpdateRequest(BY_TYPE_NOTIFICATION.getId(), true),
                new NotificationUpdateRequest(SPECIAL_NOTIFICATION.getId(), true),
                new NotificationUpdateRequest(REMAINING_NOTIFICATION.getId(), true)
        ));

        final List<NotificationResponse> notificationResponses = List.of(
                NotificationResponse.of(BY_TYPE_NOTIFICATION),
                NotificationResponse.of(SPECIAL_NOTIFICATION),
                NotificationResponse.of(REMAINING_NOTIFICATION)
        );

        when(notificationService.updateNotifications(any()))
                .thenReturn(notificationResponses);

        // when
        final ResultActions resultActions = mockMvc.perform(put("/api/v1/notifications")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationsUpdateRequest))
                .header(AUTHORIZATION, MEMBER_TOKENS));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("notifications[0].id")
                                        .type(NUMBER)
                                        .description("알림 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("notifications[0].isChecked")
                                        .type(BOOLEAN)
                                        .description("알림 여부")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("notifications[1].id")
                                        .type(NUMBER)
                                        .description("알림 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("notifications[1].isChecked")
                                        .type(BOOLEAN)
                                        .description("알림 여부")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("notifications[2].id")
                                        .type(NUMBER)
                                        .description("알림 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("notifications[2].isChecked")
                                        .type(BOOLEAN)
                                        .description("알림 여부")
                                        .attributes(field("constraint", "불리언"))
                        ),
                        responseFields(
                                fieldWithPath("[0].id")
                                        .type(NUMBER)
                                        .description("알림 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[0].notificationType")
                                        .type(STRING)
                                        .description("알림 종류")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[0].isChecked")
                                        .type(BOOLEAN)
                                        .description("알림 여부")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("[1].id")
                                        .type(NUMBER)
                                        .description("알림 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[1].notificationType")
                                        .type(STRING)
                                        .description("알림 종류")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[1].isChecked")
                                        .type(BOOLEAN)
                                        .description("알림 여부")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("[2].id")
                                        .type(NUMBER)
                                        .description("알림 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[2].notificationType")
                                        .type(STRING)
                                        .description("알림 종류")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[2].isChecked")
                                        .type(BOOLEAN)
                                        .description("알림 여부")
                                        .attributes(field("constraint", "불리언"))
                        )
                ));
    }
}
