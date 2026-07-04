package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film uppdateFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            throw new ValidationException("ID должен быть указан");
        }
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильма с указанным ID не найдено");
        }

        Film newFilm = films.get(film.getId());
        updateFields(newFilm, film);
        return newFilm;
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() != null && film.getDuration() <= 0) {
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }
    }

    private void updateFields(Film newFilm, Film film) {
        if (film.getName() != null) {
            if (film.getName().isBlank()) {
                throw new ValidationException("название не может быть пустым");
            }
            newFilm.setName(film.getName());
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() > 200) {
                throw new ValidationException("максимальная длина описания — 200 символов");
            }
            newFilm.setDescription(film.getDescription());
        }
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
                throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
            }
            newFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getDuration() != null) {
            if (film.getDuration() <= 0) {
                throw new ValidationException("продолжительность фильма должна быть положительным числом");
            }
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
