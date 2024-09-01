package dough.signout.controller;

import dough.signout.dto.request.SignoutRequestDTO;
import dough.signout.dto.response.SignoutResponseDTO;
import dough.signout.service.SignoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SignoutController {

    private final SignoutService signoutService;

    @DeleteMapping("/signout")
    public ResponseEntity<SignoutResponseDTO> signout(@RequestBody SignoutRequestDTO signoutRequestDTO) {
        SignoutResponseDTO signoutResponseDTO = signoutService.signout(signoutRequestDTO);
        return ResponseEntity.ok(signoutResponseDTO);
    }
}
