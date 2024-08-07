package dough.logout.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.logout.dto.request.DeleteAccessTokenRequest;
import dough.logout.dto.response.DeleteAccessTokenResponse;
import dough.logout.service.LogoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = LogoutController.class)
@AutoConfigureMockMvc
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LogoutService logoutService;

    private DeleteAccessTokenRequest deleteAccessTokenRequest;
    private DeleteAccessTokenResponse deleteAccessTokenResponse;

    @BeforeEach
    public void setUp() {
        deleteAccessTokenRequest = new DeleteAccessTokenRequest();
        deleteAccessTokenRequest.setAccessToken("dummyAccessToken");

        deleteAccessTokenResponse = new DeleteAccessTokenResponse(1L, "dummyNickname");
    }

    @Test
    @WithMockUser
    public void testLogout() throws Exception {
        // Given
        given(logoutService.logout(any(DeleteAccessTokenRequest.class))).willReturn(deleteAccessTokenResponse);

        // When & Then
        mockMvc.perform(delete("/api/v1/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteAccessTokenRequest))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nickname").value("dummyNickname"));
    }
}
