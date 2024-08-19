package dough.quest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestUpdateRequest {

    @NotBlank(message = "퀘스트 내용을 입력해주세요.")
    private String description;

    @NotBlank(message = "퀘스트 타입을 입력해주세요.")
    private String questType;

    @NotNull(message = "퀘스트 난이도를 입력해주세요.")
    private Integer difficulty;

    @NotNull(message = "퀘스트가 밖에서 진행되는지 여부를 입력해주세요.")
    private Boolean isOutside;

    @NotNull(message = "퀘스트가 다른 사람과 함께 수행되는지 여부를 입력해주세요.")
    private Boolean isGroup;

    @NotNull(message = "번아웃 이름를 입력해주세요.")
    private String burnoutName;
}
