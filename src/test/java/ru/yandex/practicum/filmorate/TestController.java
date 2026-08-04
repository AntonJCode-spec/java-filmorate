package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.storage.*;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final FilmLikeStorage likeStorage;

    @DeleteMapping("/clear")
    public void clearAll() {
        filmStorage.clear();
        userStorage.clear();
        friendStorage.clear();
        likeStorage.clear();
    }
}
