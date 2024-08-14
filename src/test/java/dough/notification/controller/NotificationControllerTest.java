package dough.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.AbstractControllerTest;
import dough.notification.dto.response.NotificationResponse;
import dough.notification.service.NotificationService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class NotificationControllerTest extends AbstractControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @DisplayName("멤버의 전체 알림을 조회할 수 있다.")
    @Test
    void getAllNotifications() throws Exception {
        // given
        final Long memberId = 1L;
        final List<NotificationResponse> notificationResponses = List.of(
                NotificationResponse.of(DAILY_NOTIFICATION),
                NotificationResponse.of(REMAINING_NOTIFICATION),
                NotificationResponse.of(SPECIAL_NOTIFICATION)
        );

        when(notificationService.getAllNotifications(anyLong()))
                .thenReturn(notificationResponses);

        // when
        final ResultActions resultActions = mockMvc.perform(get("/api/v1/notifications/{memberId}", memberId));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId")
                                        .description("멤버 아이디")
                        ),
                        responseFields(
                                fieldWithPath("[0].notificationType")
                                        .type(STRING)
                                        .description("알림 종류")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[0].isChecked")
                                        .type(BOOLEAN)
                                        .description("알림 여부")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("[1].notificationType")
                                        .type(STRING)
                                        .description("알림 종류")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[1].isChecked")
                                        .type(BOOLEAN)
                                        .description("알림 여부")
                                        .attributes(field("constraint", "불리언")),
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

    @DisplayName("모든 알람을 업데이트 할 수 있다.")
    @Test
    void updateAllNotifications() throws Exception {
        // given
        final Long memberId = 1L;

        doNothing().when(notificationService).updateAllNotifications(anyLong());

        // when
        final ResultActions resultActions = mockMvc.perform(put("/api/v1/notifications/{memberId}", memberId));

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId")
                                        .description("멤버 아이디")
                        )
                ));
    }

    @DisplayName("단일 알람을 업데이트 할 수 있다.")
    @Test
    void updateNotification() throws Exception {
        // given
        final Long notificationId = 1L;

        doNothing().when(notificationService).updateAllNotifications(anyLong());

        // when
        final ResultActions resultActions = mockMvc.perform(put("/api/v1/notifications/notification/{notificationId}", notificationId));

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("notificationId")
                                        .description("알림 아이디")
                        )
                ));
    }
}
