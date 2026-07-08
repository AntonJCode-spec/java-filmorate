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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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
            log.warn("Ошибка валидации email. Email не может быть пустым");
            throw new ValidationException("электронная почта не может быть пустой");
        }
        validateEmail(user.getEmail());
        log.trace("Валидация email прошла успешно");
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Ошибка валидации login. Login не может быть пустым");
            throw new ValidationException("логин не может быть пустым");
        }
        validateLogin(user.getLogin());
        log.trace("Валидация login прошла успешно");
        validateBirthday(user.getBirthday());
        log.trace("Валидация birthday прошла успешно");

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Было передано пустое имя. По умолчанию установлено значение поля login \"{}\"", user.getLogin());
        }
        user.setId(getNextId());
        log.debug("Установлен ID для нового пользователя. ID: \"{}\"", user.getId());

        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен в базу");
        log.debug("Новый пользователь: [{}]", user);
        return user;

    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            log.warn("Ошибка валидации ID. ID не был передан в запросе");
            throw new ValidationException("ID должен быть указан");
        }

        if (!users.containsKey(user.getId())) {
            log.warn("Указанный ID \"{}\" отсутствует в базе", user.getId());
            throw new NotFoundException("Пользователя с таким ID не существует");
        }

        User newUser = users.get(user.getId());

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            validateEmail(user.getEmail());
            newUser.setEmail(user.getEmail());
            log.debug("Пользователь с ID \"{}\", обновил email. Новый email \"{}\"", newUser.getId(), newUser.getEmail());
        }
        if (user.getLogin() != null && !user.getLogin().isBlank()) {
            validateLogin(user.getLogin());
            newUser.setLogin(user.getLogin());
            log.debug("Пользователь с ID \"{}\", обновил login. Новый login \"{}\"", newUser.getId(), newUser.getLogin());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            newUser.setName(user.getName());
            log.debug("Пользователь с ID \"{}\", обновил Имя для отображения. Новое Имя \"{}\"", newUser.getId(), newUser.getName());
        }
        if (user.getBirthday() != null) {
            validateBirthday(user.getBirthday());
            newUser.setBirthday(user.getBirthday());
            log.debug("Пользователь с ID \"{}\", обновил дату рождения. Новая дата \"{}\"", newUser.getId(), newUser.getBirthday());
        }
        return newUser;
    }

    private void validateEmail(String email) {
        if (!email.contains("@")) {
            log.warn("Ошибка валидации email. Был передан email \"{}\"", email);
            throw new ValidationException("электронная почта должна содержать символ @");
        }
    }

    private void validateLogin(String login) {
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

    private Long getNextId() {
        long currentMax = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMax;
    }
}
