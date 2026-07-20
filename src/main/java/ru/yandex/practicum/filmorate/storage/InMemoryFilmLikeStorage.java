package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Slf4j
@Component
public class InMemoryFilmLikeStorage implements FilmLikeStorage {
    Map<Long, Set<Long>> likeStorage = new HashMap<>();

    @Override
    public void addLike(Long filmId, Long userId) {
        Set<Long> filmLikes = likeStorage.computeIfAbsent(filmId, k -> new HashSet<>());

        if (filmLikes.contains(userId)) {
            log.debug("Пользователь с id {} уже ставил лайк фильму с id {}", userId, filmId);
            return;
        }
        filmLikes.add(userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        likeStorage.computeIfPresent(filmId, (k, v) -> {
            if (v.remove(userId)) {
                log.debug("Пользователь с id {} удалил лайк у фильма с id {}", userId, filmId);
            } else {
                log.debug("Пользователь с id {} не ставил лайк фильму с id {}", userId, filmId);
            }
            return v.isEmpty() ? null : v;
        });
    }

    @Override
    public List<Long> getTopFilmsByLike(Long topSize) {
        return likeStorage.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .map(Map.Entry::getKey)
                .limit(topSize)
                .toList();
    }

    @Override
    public void clear() {
        likeStorage.clear();
    }
}
