package dough.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.AbstractControllerTest;
import dough.member.dto.request.BurnoutTypeRequest;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberInfoResponse;
import dough.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.ResultActions;

import static dough.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class MemberControllerTest extends AbstractControllerTest {

    // TODO 추후에 token 추가

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private ResultActions performPutUpdateMemberInfoRequest(
            final Long memberId,
            final MemberInfoRequest memberInfoRequest
    ) throws Exception {
        return mockMvc.perform(put("/api/v1/members/{memberId}", memberId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberInfoRequest)));
    }

    private ResultActions performPutChangeBurnoutType(
            final Long memberId,
            final BurnoutTypeRequest burnoutTypeRequest
    ) throws Exception {
        return mockMvc.perform(put("/api/v1/members/{memberId}/burnoutType", memberId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(burnoutTypeRequest)));

    }

    @DisplayName("멤버의 닉네임을 조회할 수 있다.")
    @Test
    void getMemberInfo() throws Exception {
        // given
        Long id = 1L;
        final MemberInfoResponse memberInfoResponse = new MemberInfoResponse(id, "goeun");

        when(memberService.getMemberInfo(anyLong()))
                .thenReturn(memberInfoResponse);

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/members/{memberId}", id));

        // when
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId")
                                        .description("멤버 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(NUMBER)
                                        .description("멤버 아이디")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("nickname")
                                        .type(STRING)
                                        .description("멤버 닉네임")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));

        // then
        verify(memberService).getMemberInfo(anyLong());
    }

    @DisplayName("멤버 닉네임을 수정할 수 있다.")
    @Test
    void updateMemberInfo() throws Exception {
        // given
        Long id = 1L;
        final MemberInfoRequest memberInfoRequest = new MemberInfoRequest("minju");
        final MemberInfoResponse memberInfoResponse = new MemberInfoResponse(id, "minju");

        when(memberService.updateMemberInfo(anyLong(), any()))
                .thenReturn(memberInfoResponse);

        final ResultActions resultActions = performPutUpdateMemberInfoRequest(id, memberInfoRequest);

        // when
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId")
                                        .description("멤버 아이디")
                        ),
                        requestFields(
                                fieldWithPath("nickname")
                                        .type(STRING)
                                        .description("멤버 닉네임")
                                        .attributes(field("constraint", "문자열"))
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(NUMBER)
                                        .description("멤버 아이디")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("nickname")
                                        .type(STRING)
                                        .description("멤버 닉네임")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));

        // then
        verify(memberService).updateMemberInfo(anyLong(), any());
    }

    @DisplayName("닉네임이 5자를 초과할 경우 예외가 발생한다.")
    @Test
    void updateMemberInfo_NicknameSizeInvalid() throws Exception {
        // given
        final MemberInfoRequest memberInfoRequest = new MemberInfoRequest("jjanggu");

        // when
        final ResultActions resultActions = performPutUpdateMemberInfoRequest(1L, memberInfoRequest);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("닉네임은 5자를 초과할 수 없습니다."));
    }

    @DisplayName("멤버의 번아웃 유형을 수정할 수 있다.")
    @Test
    void changeBurnoutType() throws Exception {
        // given
        Long id = 1L;
        final BurnoutTypeRequest burnoutTypeRequest = new BurnoutTypeRequest("호빵");

        doNothing().when(memberService).changeBurnoutType(anyLong(), any());

        // when
        final ResultActions resultActions = performPutChangeBurnoutType(id, burnoutTypeRequest);

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId")
                                        .description("멤버 아이디")
                        ),
                        requestFields(
                                fieldWithPath("burnoutType")
                                        .type(STRING)
                                        .description("번아웃 유형")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }

    @DisplayName("번아웃 유형이 null일 경우 예외가 발생한다.")
    @Test
    void changeBurnoutType_BurnoutTypeNull() throws Exception {
        // given
        Long id = 1L;
        final BurnoutTypeRequest burnoutTypeRequest = new BurnoutTypeRequest(null);

        doNothing().when(memberService).changeBurnoutType(anyLong(), any());

        // when
        final ResultActions resultActions = performPutChangeBurnoutType(id, burnoutTypeRequest);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("번아웃 타입을 입력해주세요."));
    }
}
