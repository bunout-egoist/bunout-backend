package dough.level.service;

import dough.global.exception.BadRequestException;
import dough.level.domain.Level;
import dough.level.domain.MemberLevel;
import dough.level.domain.repository.LevelRepository;
import dough.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static dough.global.exception.ExceptionCode.NOT_FOUND_LEVEL_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class LevelService {

    private final LevelRepository levelRepository;

    public MemberLevel updateLevel(final Member member) {
        final Level currentLevel = member.getLevel();

        if (member.getExp() >= currentLevel.getRequiredExp()) {
            final List<Level> levels = findCurrentAndNextLevel(currentLevel.getLevel() + 1);
            member.updateLevel(levels.get(0));
            return new MemberLevel(member, levels, true);
        }

        final List<Level> levels = findCurrentAndNextLevel(currentLevel.getLevel());
        return new MemberLevel(member, levels, false);
    }

    private List<Level> findCurrentAndNextLevel(final Integer level) {
        final List<Level> levels = levelRepository.findCurrentAndNextLevel(level)
                .stream()
                .sorted(Comparator.comparing(Level::getLevel))
                .collect(Collectors.toList());

        if (levels.isEmpty()) {
            throw new BadRequestException(NOT_FOUND_LEVEL_ID);
        }

        return levels;
    }
}
