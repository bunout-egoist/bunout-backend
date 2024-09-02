package dough.level.service;

import dough.global.exception.BadRequestException;
import dough.level.domain.Level;
import dough.level.domain.MemberLevel;
import dough.level.domain.repository.LevelRepository;
import dough.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static dough.global.exception.ExceptionCode.NOT_FOUND_LEVEL_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class LevelService {

    private final LevelRepository levelRepository;

    public MemberLevel updateLevel(final Member member) {
        final Level currentLevel = member.getLevel();

        if (member.getExp() >= currentLevel.getAccumulatedExp()) {
            final List<Level> levels = levelRepository.findTopByExp(member.getExp(), PageRequest.of(0, 1));

            if (levels.isEmpty()) {
                throw new BadRequestException(NOT_FOUND_LEVEL_ID);
            }

            final Level newLevel = levels.get(0);
            member.updateLevel(newLevel);
            return new MemberLevel(member, newLevel, true);
        }

        return new MemberLevel(member, currentLevel, false);
    }
}