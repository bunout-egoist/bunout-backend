package dough.global.member.controller;


import dough.global.AbstractControllerTest;
import dough.member.controller.MemberController;
import dough.member.dto.response.MemberInfoResponse;
import dough.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.ResultActions;

import static dough.global.restdocs.RestDocsConfiguration.field;
import static javax.management.openmbean.SimpleType.LONG;
import static javax.management.openmbean.SimpleType.STRING;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class MemberControllerTest extends AbstractControllerTest {

    // TODO 추후에 token 추가
    @MockBean
    private MemberService memberService;

    @DisplayName("멤버의 닉네임을 조회할 수 있다.")
    @Test
    void getMemberInfo() throws Exception {
        // given
        Long id = 1L;
        final MemberInfoResponse memberInfoResponse = new MemberInfoResponse(id, "jjanggu");

        when(memberService.getMemberInfo(id))
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
        verify(memberService).getMemberInfo(id);
    }
}
