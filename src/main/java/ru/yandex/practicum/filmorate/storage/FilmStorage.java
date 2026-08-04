package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getFilms();

    Optional<Film> getFilmById(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film deleteFilm(Long id);

    boolean isFilmExist(Long filmId);

    void clear();
}
