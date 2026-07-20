package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User addUser(User user) {
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

    @Override
    public User updateUser(User user) {

        if (!users.containsKey(user.getId())) {
            log.warn("Указанный ID \"{}\" отсутствует в базе", user.getId());
            throw new UserNotFoundException("Пользователя с таким ID не существует");
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

    @Override
    public User deleteUser(Long id) {
        return null;
    }

    @Override
    public boolean isUserExist(Long id) {
        return users.containsKey(id);
    }

    @Override
    public void clear() {
        users.clear();
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
