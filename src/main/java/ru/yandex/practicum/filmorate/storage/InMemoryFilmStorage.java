package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextId());
        log.debug("Установлен ID {}", film.getId());
        films.put(film.getId(), film);
        log.info("Добавленный фильм: [{}]", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Полученный в запросе ID не содержится в базе. Получен ID \"{}\"", film.getId());
            throw new NotFoundException("Фильма с указанным ID не найдено");
        }

        Film newFilm = films.get(film.getId());
        updateFields(newFilm, film);
        log.info("Значения фильма с ID \"{}\" успешно обновлены", newFilm.getId());
        return newFilm;
    }

    @Override
    public Film deleteFilm(Long id) {
        return null;
    }

    @Override
    public boolean isFilmExist(Long filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public void clear() {
        films.clear();
    }

    private void updateFields(Film newFilm, Film film) {
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
