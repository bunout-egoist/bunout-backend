package dough.level.service;

import dough.global.exception.BadRequestException;
import dough.level.domain.Level;
import dough.level.domain.MemberLevel;
import dough.level.domain.repository.LevelRepository;
import dough.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static dough.global.exception.ExceptionCode.NOT_FOUND_LEVEL_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class LevelService {

    private final LevelRepository levelRepository;

    public MemberLevel updateLevel(final Member member) {
        final Level currentLevel = member.getLevel();

        if (member.getExp() >= currentLevel.getRequiredExp()) {
            final Level level = levelRepository.findByLevel(currentLevel.getLevel() + 1)
                    .orElseThrow(() -> new BadRequestException(NOT_FOUND_LEVEL_ID));
            member.updateLevel(level);
            return new MemberLevel(member, currentLevel.getLevel(), true);
        }

        return new MemberLevel(member, currentLevel.getLevel(), false);
    }
}
