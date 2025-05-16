package pokemon.pokedex.__testutils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pokemon.pokedex._common.session.registry.MemorySessionRegistry;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex.user.repository.MemoryUserRepository;
import pokemon.pokedex.user.repository.UserRepository;

@Slf4j
@Component
@Profile("test")
public class ClearMemory {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private TestDataInit testDataInit;

    public void clearMemory() {
        log.info("[clearMemory] --- 메모리 저장소 초기화 시작 ---");
        if (userRepository instanceof MemoryUserRepository) {
            log.info("[clearUserRepository] --- UserRepository 메모리 저장소 초기화 시작 ---");
            ((MemoryUserRepository) userRepository).clear();
            testDataInit.initUserRepository();
            log.info("[clearUserRepository] --- UserRepository 메모리 저장소 초기화 완료 ---");
        }

        if (sessionRegistry instanceof MemorySessionRegistry) {
            log.info("[clearSessionRegistry] --- SessionRegistry 메모리 저장소 초기화 시작 ---");
            ((MemorySessionRegistry) sessionRegistry).clear();
            testDataInit.initSessionRegistry();
            log.info("[clearSessionRegistry] --- SessionRegistry 메모리 저장소 초기화 완료 ---");
        }
        log.info("[clearMemory] --- 메모리 저장소 초기화 완료 ---\n");

    }
}
