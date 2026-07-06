package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        if (film.getName() == null) {
            log.warn("Название фильма должно быть передано");
            throw new ValidationException("Название фильма должно быть указано");
        }
        validateFilm(film);
        log.trace("Валидация прошла успешно");
        film.setId(getNextId());
        log.debug("Установлен ID {}", film.getId());
        films.put(film.getId(), film);
        log.info("Добавленный фильм: [{}]", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            log.warn("Ошибка валидации ID. ID не был передан в запросе");
            throw new ValidationException("ID должен быть указан");
        }
        if (!films.containsKey(film.getId())) {
            log.warn("Полученный в запросе ID не содержится в базе. Получен ID \"{}\"", film.getId());
            throw new NotFoundException("Фильма с указанным ID не найдено");
        }

        Film newFilm = films.get(film.getId());
        updateFields(newFilm, film);
        log.info("Значения фильма с ID \"{}\" успешно обновлены", newFilm.getId());
        return newFilm;
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getName() != null && film.getName().isBlank()) {
            log.warn("Ошибка валидации названия. Было передано \"{}\"", film.getName());
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Ошибка валидации описания. Переданная длина описания \"{}\"", film.getDescription().length());
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.warn("ошибка валидации даты релиза фильма. Была получена дата релиза \"{}\"", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() != null && film.getDuration() <= 0) {
            log.warn("Ошибка валидации продолжительности фильма. Полученное значение \"{}\"", film.getDuration());
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }
    }

    private void updateFields(Film newFilm, Film film) {
        validateFilm(film);
        if (film.getName() != null) {
            log.debug("Для фильма с ID {} установлено новое название \"{}\"", film.getId(), film.getName());
            newFilm.setName(film.getName());
        }
        if (film.getDescription() != null) {
            log.debug("Для фильма с ID {} установлено новое описание \"{}\"", film.getId(), film.getDescription());
            newFilm.setDescription(film.getDescription());
        }
        if (film.getReleaseDate() != null) {
            log.debug("Для фильма с ID {} установлена новая дата релиза \"{}\"", film.getId(), film.getReleaseDate());
            newFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getDuration() != null) {
            log.debug("Для фильма с ID {} установлена новая продолжительность \"{}\"", film.getId(), film.getDuration());
            newFilm.setDuration(film.getDuration());
        }
    }

    private Long getNextId() {
        long currentMax = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMax;
    }
}
