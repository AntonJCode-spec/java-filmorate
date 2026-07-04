package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("электронная почта не может быть пустой");
        }
        validateEmail(user.getEmail());
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("логин не может быть пустым");
        }
        validateLogin(user.getLogin());
        validateBirthday(user.getBirthday());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());

        users.put(user.getId(), user);
        return user;

    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователя с таким ID не существует");
        }

        User newUser = users.get(user.getId());

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            validateEmail(user.getEmail());
            newUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null && !user.getLogin().isBlank()) {
            validateLogin(user.getLogin());
            newUser.setLogin(user.getLogin());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            newUser.setName(user.getName());
        }
        if (user.getBirthday() != null) {
            validateBirthday(user.getBirthday());
            newUser.setBirthday(user.getBirthday());
        }

        return newUser;
    }

    private void validateEmail(String email) {
        if (!email.contains("@")) {
            throw new ValidationException("электронная почта должна содержать символ @");
        }
    }

    private void validateLogin(String login) {
        if (login.trim().contains(" ")) {
            throw new ValidationException("логин не может содержать пробелы");
        }
    }

    private void validateBirthday(LocalDate birthday) {
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("дата рождения не может быть в будущем");
        }
    }

    private Long getNextId() {
        long currentMax = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMax;
    }
}
