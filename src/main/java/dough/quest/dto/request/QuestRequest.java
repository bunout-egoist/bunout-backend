package dough.quest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestRequest {

    @NotBlank(message = "퀘스트 설명을 입력해주세요.")
    private String description;

    @NotBlank(message = "퀘스트 활동 내용을 입력해주세요.")
    private String activity;

    @NotBlank(message = "퀘스트 타입을 입력해주세요.")
    private String questType;

    @NotBlank(message = "퀘스트 난이도를 입력해주세요.")
    private Integer difficulty;
}
