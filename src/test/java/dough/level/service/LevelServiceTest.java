package dough.level.service;

import dough.level.domain.MemberLevel;
import dough.level.domain.repository.LevelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static dough.level.fixture.LevelFixture.LEVEL2;
import static dough.member.fixture.MemberFixture.GOEUN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        GOEUN.updateExp(25);

        given(levelRepository.findTopByExp(anyInt(), any()))
                .willReturn(List.of(LEVEL2));

        // when
        final MemberLevel actualMemberLevel = levelService.updateLevel(GOEUN);

        // then
        assertThat(actualMemberLevel).usingRecursiveComparison()
                .isEqualTo(new MemberLevel(GOEUN, LEVEL2, true, 5));
    }
}
