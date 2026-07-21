package ru.yandex.practicum.filmorate;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.storage.*;

@RestController
@RequestMapping("/test")
public class TestController {

    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;
    private final InMemoryFriendStorage friendStorage;
    private final InMemoryFilmLikeStorage likeStorage;

    public TestController(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage,
                          InMemoryFriendStorage friendStorage, InMemoryFilmLikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
        this.likeStorage = likeStorage;
    }

    @DeleteMapping("/clear")
    public void clearAll() {
        filmStorage.clear();
        userStorage.clear();
        friendStorage.clear();
        likeStorage.clear();
    }
}
