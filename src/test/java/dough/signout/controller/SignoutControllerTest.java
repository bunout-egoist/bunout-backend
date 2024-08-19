package dough.signout.controller;

import dough.signout.dto.request.SignoutRequestDTO;
import dough.signout.dto.response.SignoutResponseDTO;
import dough.signout.service.SignoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SignoutControllerTest {

    @Mock
    private SignoutService signoutService;

    @InjectMocks
    private SignoutController signoutController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignout() {
        // Given
        String testToken = "testToken123";  // Replace with an appropriate test token if needed
        SignoutRequestDTO signoutRequestDTO = new SignoutRequestDTO(testToken);
        SignoutResponseDTO signoutResponseDTO = SignoutResponseDTO.of(1L);

        // When
        when(signoutService.signout(any(SignoutRequestDTO.class))).thenReturn(signoutResponseDTO);
        ResponseEntity<SignoutResponseDTO> responseEntity = signoutController.signout(signoutRequestDTO);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1L, responseEntity.getBody().getMemberId());

    }

}
