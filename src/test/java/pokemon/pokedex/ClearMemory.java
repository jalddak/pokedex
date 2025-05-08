package pokemon.pokedex;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import pokemon.pokedex._global.session.registry.MemorySessionRegistry;
import pokemon.pokedex._global.session.registry.SessionRegistry;
import pokemon.pokedex.user.repository.MemoryUserRepository;
import pokemon.pokedex.user.repository.UserRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ClearMemory {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected SessionRegistry sessionRegistry;

    @BeforeEach
    void clearMemory_beforeEach() {
        if (userRepository instanceof MemoryUserRepository) {
            ((MemoryUserRepository) userRepository).clear();
        }

        if (sessionRegistry instanceof MemorySessionRegistry) {
            ((MemorySessionRegistry) sessionRegistry).clear();
        }
    }

    @AfterEach
    void clearMemory_afterEach() {
        if (userRepository instanceof MemoryUserRepository) {
            ((MemoryUserRepository) userRepository).clear();
        }

        if (sessionRegistry instanceof MemorySessionRegistry) {
            ((MemorySessionRegistry) sessionRegistry).clear();
        }
    }
}
