package pokemon.pokedex;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import pokemon.pokedex._common.session.registry.MemorySessionRegistry;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex.user.repository.MemoryUserRepository;
import pokemon.pokedex.user.repository.UserRepository;

@Slf4j
public abstract class ClearMemory {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected SessionRegistry sessionRegistry;

    @Autowired
    protected TestDataInit testDataInit;

    @BeforeEach
    void clearMemory_beforeEach() {
        log.info("--- 메모리 저장소 초기화 시작 ---");
        if (userRepository instanceof MemoryUserRepository) {
            ((MemoryUserRepository) userRepository).clear();
            testDataInit.initUserRepository();
        }

        if (sessionRegistry instanceof MemorySessionRegistry) {
            ((MemorySessionRegistry) sessionRegistry).clear();
            testDataInit.initSessionRegistry();
        }
        log.info("--- 메모리 저장소 초기화 완료 ---");

    }
}
