package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не существует"));
    }

    public Set<User> getUserFriends(Long userId) {
        validateUser(userId);
        return friendStorage.getUserFriends(userId).stream()
                .map(userStorage::getUserById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public User addUser(User user) {
        validateUserEmail(user.getEmail());
        log.trace("Валидация email прошла успешно");
        validateUserLogin(user.getLogin());
        log.trace("Валидация login прошла успешно");
        validateBirthday(user.getBirthday());
        log.trace("Валидация birthday прошла успешно");
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            log.warn("Ошибка валидации ID. ID не был передан в запросе");
            throw new ValidationException("ID должен быть указан");
        }

        return userStorage.updateUser(user);
    }

    public void addFriend(Long userId, Long friendId) {
        validateUser(userId);
        validateUser(friendId);

        if (userId.equals(friendId)) {
            log.debug("Пользователь с id {} пытается добавить в друзья самого себя", userId);
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        friendStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        validateUser(userId);
        validateUser(friendId);
        friendStorage.removeFriend(userId, friendId);
    }

    public List<User> getMutualFriends(Long userId, Long friendId) {
        validateUser(userId);
        validateUser(friendId);

        Set<Long> friendsId = friendStorage.getMutualFriends(userId, friendId);
        return friendsId.stream()
                .map(userStorage::getUserById)
                .flatMap(Optional::stream)
                .toList();
    }

    private void validateUser(Long userId) {
        if (!userStorage.isUserExist(userId)) {
            log.debug("Пользователя с id {} не существует", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    private void validateUserEmail(String email) {
        if (email == null || email.isBlank()) {
            log.warn("Ошибка валидации email. Email не может быть пустым");
            throw new ValidationException("электронная почта не может быть пустой");
        }
        if (!email.contains("@")) {
            log.warn("Ошибка валидации email. Был передан email \"{}\"", email);
            throw new ValidationException("электронная почта должна содержать символ @");
        }
    }

    private void validateUserLogin(String login) {
        if (login == null || login.isBlank()) {
            log.warn("Ошибка валидации login. Login не может быть пустым");
            throw new ValidationException("логин не может быть пустым");
        }
        if (login.trim().contains(" ")) {
            log.warn("Ошибка валидации login. Был передан login \"{}\"", login);
            throw new ValidationException("логин не может содержать пробелы");
        }
    }

    private void validateBirthday(LocalDate birthday) {
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации birthday. Был передан birthday \"{}\"", birthday);
            throw new ValidationException("дата рождения не может быть в будущем");
        }
    }
}
