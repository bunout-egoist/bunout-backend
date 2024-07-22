package dough.global.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.AbstractControllerTest;
import dough.member.controller.MemberController;
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
import static javax.management.openmbean.SimpleType.LONG;
import static javax.management.openmbean.SimpleType.STRING;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
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
            final MemberInfoRequest memberInfoRequest)
            throws Exception {
        return mockMvc.perform(put("/api/v1/members/{memberId}", memberId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberInfoRequest)));
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
                                        .type(LONG)
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
                                        .type(LONG)
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
}
