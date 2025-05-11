package pokemon.pokedex._common.session;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pokemon.pokedex._common.session.registry.MemorySessionRegistry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionListener implements HttpSessionListener {

    private final MemorySessionRegistry memorySessionRegistry;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.debug("[{}] SessionListener sessionDestroyed 실행", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        memorySessionRegistry.removeSession(se.getSession());
    }
}
