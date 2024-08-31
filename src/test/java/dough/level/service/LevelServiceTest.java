package dough.level.service;

import dough.global.exception.BadRequestException;
import dough.level.domain.MemberLevel;
import dough.level.domain.repository.LevelRepository;
import dough.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static dough.burnout.fixture.BurnoutFixture.SOBORO;
import static dough.global.exception.ExceptionCode.NOT_FOUND_LEVEL_ID;
import static dough.level.fixture.LevelFixture.LEVEL1;
import static dough.level.fixture.LevelFixture.LEVEL2;
import static dough.login.domain.type.RoleType.MEMBER;
import static dough.login.domain.type.SocialLoginType.KAKAO;
import static dough.quest.fixture.QuestFixture.FIXED_QUEST1;
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
        final Member member = new Member(
                1L,
                "goeun",
                "0000",
                KAKAO,
                "goeun@mail.com",
                "기타",
                "여성",
                2002,
                SOBORO,
                MEMBER,
                LEVEL1,
                FIXED_QUEST1
        );

        member.updateExp(25);

        given(levelRepository.findCurrentAndNextLevel(anyInt()))
                .willReturn(List.of(LEVEL1, LEVEL2));

        // when
        final MemberLevel actualMemberLevel = levelService.updateLevel(member);

        // then
        assertThat(actualMemberLevel).usingRecursiveComparison()
                .isEqualTo(new MemberLevel(member, List.of(LEVEL1, LEVEL2), true));
    }

    @DisplayName("레벨이 존재하지 않을 경우 예외가 발생한다.")
    @Test
    void updateLevel_NotFoundLevelId() {
        // given
        final Member member = new Member(
                1L,
                "goeun",
                "0000",
                KAKAO,
                "goeun@mail.com",
                "기타",
                "여성",
                2002,
                SOBORO,
                MEMBER,
                LEVEL1,
                FIXED_QUEST1
        );

        member.updateExp(25);

        given(levelRepository.findCurrentAndNextLevel(2))
                .willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> levelService.updateLevel(member))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(NOT_FOUND_LEVEL_ID.getCode());
    }
}
