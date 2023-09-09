package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> uniqEmails = new HashSet<>();

    private long userId = 0;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.of(users.get(id));
    }

    @Override
    public User createUser(User user) {
        if (!user.getEmail().isEmpty())
            uniqEmails.add(user.getEmail());
        userId++;
        user.setId(userId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        User oldUser = users.get(id);
        if (!oldUser.getEmail().equals(user.getEmail())) {
            uniqEmails.remove(oldUser.getEmail());
            uniqEmails.add(user.getEmail());
        }
        users.put(id, user);
        return user;
    }

    @Override
    public boolean deleteUserById(Long id) {
        if (users.containsKey(id)) {
            if (!users.get(id).getEmail().isEmpty())
                uniqEmails.remove(users.get(id).getEmail());
            users.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<User> isUserExist(User user) {
        if (users.containsValue(user)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Boolean isEmailExist(String email) {
        if (email.isEmpty())
            return false;
        return !uniqEmails.stream().filter(u -> u.equals(email)).findFirst().isEmpty();
    }


}
