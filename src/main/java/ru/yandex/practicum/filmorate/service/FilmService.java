package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.VariableRangeException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final UserStorage userStorage;

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильм не существует"));
    }

    public Film addFilm(Film film) {
        if (film.getName() == null) {
            throw new ValidationException("Имя фильма должно быть передано");
        }

        validateFilmFields(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.warn("Ошибка валидации ID. ID не был передан в запросе");
            throw new ValidationException("ID должен быть указан");
        }
        validateFilmFields(film);
        return filmStorage.updateFilm(film);
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        log.trace("Попытка поставить лайк фильму");
        validateUser(userId);
        validateFilm(filmId);

        filmLikeStorage.addLike(filmId, userId);
    }

    public void removeLikeFromFilm(Long filmId, Long userId) {
        log.trace("Попытка убрать лайк с фильма");
        validateUser(userId);
        validateFilm(filmId);

        filmLikeStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilmsByLike(Long topSize) {
        if (topSize <= 0) {
            log.debug("Значение переменной topSize= {}", topSize);
            throw new VariableRangeException("Значением переменной topSize должно быть положительное число");
        }

        return filmLikeStorage.getTopFilmsByLike(topSize).stream()
                .map(filmStorage::getFilmById)
                .flatMap(Optional::stream)
                .toList();
    }

    private void validateUser(Long userId) {
        if (!userStorage.isUserExist(userId)) {
            log.debug("Пользователя с id {} не существует", userId);
            throw new UserNotFoundException("Пользователь не существует");
        }
    }

    private void validateFilm(Long filmId) {
        if (!filmStorage.isFilmExist(filmId)) {
            log.debug("Фильма с id {} не существует", filmId);
            throw new FilmNotFoundException("Фильм не существует");
        }
    }

    private void validateFilmName(String name) {
        if (name != null && name.isBlank()) {
            log.warn("Ошибка валидации названия. Было передано \"{}\"", name);
            throw new ValidationException("название не может быть пустым");
        }
    }

    private void validateFilmDescription(String description) {
        if (description != null && description.length() > 200) {
            log.warn("Ошибка валидации описания. Переданная длина описания \"{}\"", description.length());
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
    }

    private void validateFilmReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && releaseDate.isBefore(FIRST_FILM_DATE)) {
            log.warn("ошибка валидации даты релиза фильма. Была получена дата релиза \"{}\"", releaseDate);
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    private void validateFilmDuration(Integer duration) {
        if (duration != null && duration <= 0) {
            log.warn("Ошибка валидации продолжительности фильма. Полученное значение \"{}\"", duration);
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }
    }

    private void validateFilmFields(Film film) {
        validateFilmName(film.getName());
        log.trace("Валидация имени прошла успешно");
        validateFilmDescription(film.getDescription());
        log.trace("Валидация описания прошла успешно");
        validateFilmReleaseDate(film.getReleaseDate());
        log.trace("Валидация даты релиза прошла успешно");
        validateFilmDuration(film.getDuration());
        log.trace("Валидация длительности фильма прошла успешно");
    }
}
