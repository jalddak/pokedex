package pokemon.pokedex._common.session;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pokemon.pokedex._common.session.registry.MemorySessionRegistry;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionListener implements HttpSessionListener {

    private final MemorySessionRegistry memorySessionRegistry;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        memorySessionRegistry.removeSession(se.getSession());
    }
}
