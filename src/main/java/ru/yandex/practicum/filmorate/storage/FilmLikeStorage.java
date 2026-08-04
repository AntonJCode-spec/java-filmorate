package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface FilmLikeStorage {

        void addLike(Long filmId, Long userId);

        void deleteLike(Long filmId, Long userId);

        List<Long> getTopFilmsByLike(Long topSize);

        void clear();
}
