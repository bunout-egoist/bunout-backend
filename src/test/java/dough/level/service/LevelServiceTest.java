package dough.level.service;

import dough.global.exception.BadRequestException;
import dough.level.domain.MemberLevel;
import dough.level.domain.repository.LevelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static dough.global.exception.ExceptionCode.NOT_FOUND_LEVEL_ID;
import static dough.level.fixture.LevelFixture.LEVEL2;
import static dough.member.fixture.MemberFixture.GOEUN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Transactional
public class LevelServiceTest {

    @InjectMocks
    private LevelService levelService;

    @Mock
    private LevelRepository levelRepository;

    @DisplayName("멤버는 레벨업을 할 수 있다.")
    @Test
    void updateLevel() {
        // given
        GOEUN.updateExp(45);

        given(levelRepository.findByLevel(anyInt()))
                .willReturn(Optional.of(LEVEL2));

        // when
        final MemberLevel actualMemberLevel = levelService.updateLevel(GOEUN);

        // then
        assertThat(actualMemberLevel).usingRecursiveComparison()
                .isEqualTo(new MemberLevel(GOEUN, 1, true));
    }

    @DisplayName("레벨이 존재하지 않을 경우 예외가 발생한다.")
    @Test
    void updateLevel_NotFoundLevelId() {
        // given
        GOEUN.updateExp(50);

        given(levelRepository.findByLevel(2))
                .willThrow(new BadRequestException(NOT_FOUND_LEVEL_ID));

        // when & then
        assertThatThrownBy(() -> levelService.updateLevel(GOEUN))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(NOT_FOUND_LEVEL_ID.getCode());
    }
}
