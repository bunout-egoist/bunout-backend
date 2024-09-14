package dough.level.service;

import dough.level.domain.Level;
import dough.level.domain.MemberLevel;
import dough.level.domain.repository.LevelRepository;
import dough.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LevelService {

    private final LevelRepository levelRepository;

    public MemberLevel updateLevel(final Member member) {
        final Level currentLevel = member.getLevel();

        if (member.getExp() < currentLevel.getAccumulatedExp()) {
            return new MemberLevel(member, currentLevel, false, calculateCurrentExp(member, currentLevel));
        }

        final Level nextLevel = findNextLevel(member.getExp(), currentLevel);

        if (nextLevel.equals(currentLevel)) {
            return new MemberLevel(member, currentLevel, false, currentLevel.getRequiredExp());
        }

        member.updateLevel(nextLevel);
        return new MemberLevel(member, nextLevel, true, calculateCurrentExp(member, nextLevel));
    }

    private Level findNextLevel(final Integer memberExp, final Level currentLevel) {
        final List<Level> levels = levelRepository.findTopByExp(memberExp, PageRequest.of(0, 1));

        if (levels.isEmpty()) {
            return currentLevel;
        }

        return levels.get(0);
    }

    private Integer calculateCurrentExp(final Member member, final Level level) {
        return level.getRequiredExp() - (level.getAccumulatedExp() - member.getExp());
    }
}
