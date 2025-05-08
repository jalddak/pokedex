package pokemon.pokedex._global.session.registry;

import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface SessionRegistry {

    void addSession(Long userId, HttpSession session);

    void removeSession(HttpSession session);

    List<HttpSession> getSessionsByUserId(Long userId);
}
