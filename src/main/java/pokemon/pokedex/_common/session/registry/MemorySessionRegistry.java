package pokemon.pokedex._common.session.registry;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pokemon.pokedex._global.SessionConst;
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
        log.debug("Saved session in SessionRegistry, userId: {}, sessionId: {}", userId, session.getId());
    }

    @Override
    public void removeSession(HttpSession session) {

        SessionUserDTO sessionUserDTO = (SessionUserDTO) session.getAttribute(SessionConst.SESSION_USER_DTO);
        if (sessionUserDTO == null) {
            log.debug("Session invalidation occurred unrelated to login, sessionId: {}", session.getId());
            return;
        }
        Long userId = sessionUserDTO.getId();

        userSessionMap.computeIfPresent(userId, (key, sessions) -> {
            sessions.remove(session);
            log.debug("Deleted session in SessionRegistry, userId: {}, sessionId: {}", userId, session.getId());
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

    /**
     * 테스트에서만 사용하는 메서드
     */
    public void clear() {
        for (Long userId : userSessionMap.keySet()) {
            userSessionMap.get(userId).forEach(HttpSession::invalidate);
        }
        userSessionMap.clear();
    }
}
