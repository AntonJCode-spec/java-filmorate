package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Slf4j
@Component
public class InMemoryFriendStorage implements FriendStorage {

    private final Map<Long, Set<Long>> friendsStorage = new HashMap<>();

    @Override
    public Map<Long, Set<Long>> getFriends() {
        return Collections.unmodifiableMap(friendsStorage);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        friendsStorage.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friendsStorage.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);

        log.debug("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        friendsStorage.computeIfPresent(userId, (k, v) -> {
            v.remove(friendId);
            return v.isEmpty() ? null : v;
        });

        friendsStorage.computeIfPresent(friendId, (k, v) -> {
            v.remove(userId);
            return v.isEmpty() ? null : v;
        });
        log.debug("Пользователь с id {} удалил пользователя с id {} из списка друзей", userId, friendId);
    }

    @Override
    public Set<Long> getMutualFriends(Long userId, Long friendId) {
        Set<Long> userFriend = friendsStorage.getOrDefault(userId, Collections.emptySet());
        Set<Long> otherFriend = friendsStorage.getOrDefault(friendId, Collections.emptySet());

        Set<Long> mutualFriends = new HashSet<>(userFriend);
        mutualFriends.retainAll(otherFriend);
        return mutualFriends;
    }

    @Override
    public Set<Long> getUserFriends(Long userId) {
        return friendsStorage.getOrDefault(userId, Collections.emptySet());
    }

    @Override
    public void clear() {
        friendsStorage.clear();
    }
}
