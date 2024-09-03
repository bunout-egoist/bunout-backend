package dough.feedback.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackRequest {

    @NotNull(message = "선택된 퀘스트 아이디를 입력해주세요.")
    private Long selectedQuestId;

    @Min(1)
    @Max(5)
    @NotNull(message = "퀘스트 난이도를 입력해주세요.")
    private Integer difficulty;
}