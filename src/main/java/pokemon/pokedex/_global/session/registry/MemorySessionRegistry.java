package pokemon.pokedex._global.session.registry;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MemorySessionRegistry implements SessionRegistry {

    private final Map<Long, Set<HttpSession>> userSessionMap = new ConcurrentHashMap<>();

    @Override
    public void addSession(Long userId, HttpSession session) {
        userSessionMap.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @Override
    public void removeSession(HttpSession session) {

        SessionUserDTO sessionUserDTO = (SessionUserDTO) session.getAttribute(SessionConst.SESSION_USER_DTO);
        if (sessionUserDTO == null) return;
        Long userId = sessionUserDTO.getId();

        userSessionMap.computeIfPresent(userId, (key, sessions) -> {
            sessions.remove(session);
            log.debug("Removed session userId: {}, sessionId: {}", userId, session.getId());
            return sessions.isEmpty() ? null : sessions;
        });
    }

    @Override
    public List<HttpSession> getSessionsByUserId(Long userId) {
        return Optional.ofNullable(userSessionMap.get(userId))
                .orElse(Set.of())
                .stream()
                .toList();
    }

    public void clear() {
        userSessionMap.clear();
    }
}
