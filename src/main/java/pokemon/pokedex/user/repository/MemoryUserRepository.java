package pokemon.pokedex.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pokemon.pokedex.user.domain.User;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class MemoryUserRepository implements UserRepository {

    private static final Map<Long, User> store = new HashMap<>();
    private static long sequence = 0L;

    public void clear() {
        sequence = 0L;
        store.clear();
    }

    @Override
    public User save(User user) {
        user.setId(++sequence);
        user.setCreatedAt(LocalDateTime.now());
        store.put(user.getId(), user);
        log.info("Saving user: {}", user.getLoginId());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return findAll().stream().anyMatch(user -> user.getLoginId().equals(loginId));
    }

    @Override
    public Optional<User> findByLoginId(String loginId) {
        return findAll().stream().filter(user -> user.getLoginId().equals(loginId)).findFirst();
    }
}
