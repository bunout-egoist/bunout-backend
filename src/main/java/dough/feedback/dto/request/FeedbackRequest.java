package dough.feedback.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackRequest {


    private String imageUrl;

    @NotBlank(message = "퀘스트 난이도를 입력해주세요.")
    @Min(1)
    @Max(5)
    private Integer difficulty;
}
