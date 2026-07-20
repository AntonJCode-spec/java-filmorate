package ru.yandex.practicum.filmorate.storage;

import java.util.Map;
import java.util.Set;

public interface FriendStorage {

    Map<Long, Set<Long>> getFriends();

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Set<Long> getUserFriends(Long id);

    Set<Long> getMutualFriends(Long userId, Long friendId);

    void clear();
}
