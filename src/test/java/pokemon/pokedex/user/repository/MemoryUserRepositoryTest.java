package pokemon.pokedex.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pokemon.pokedex.user.domain.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryUserRepositoryTest {

    private MemoryUserRepository memoryUserRepository = new MemoryUserRepository();

    @AfterEach
    void tearDown() {
        memoryUserRepository.clear();
    }

    @Test
    @DisplayName("저장")
    void save() {
        User user = new User();

        User saveUser = memoryUserRepository.save(user);

        assertThat(saveUser.getId()).isNotNull();
        assertThat(saveUser.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("조회")
    void findById() {
        User user = new User();
        memoryUserRepository.save(user);

        Optional<User> findUser = memoryUserRepository.findById(1L);
        Optional<User> notUser = memoryUserRepository.findById(2L);

        assertThat(findUser.orElse(null)).isEqualTo(user);
        assertThat(notUser.isPresent()).isFalse();
    }

    @Test
    @DisplayName("전체 조회")
    void findAll() {
        User user = new User();

        memoryUserRepository.save(user);
        assertThat(memoryUserRepository.findAll().size()).isEqualTo(1);

        memoryUserRepository.save(user);
        assertThat(memoryUserRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("중복 로그인 아이디 체크")
    void existsByLoginId() {

        User user = new User();
        user.setLoginId("user");

        memoryUserRepository.save(user);

        assertThat(memoryUserRepository.existsByLoginId("user")).isTrue();
        assertThat(memoryUserRepository.existsByLoginId("not")).isFalse();
    }

    @Test
    @DisplayName("로그인 아이디로 유저 찾기")
    void findByLoginId() {
        User user = new User();
        user.setLoginId("user");

        memoryUserRepository.save(user);

        Optional<User> findUser = memoryUserRepository.findByLoginId(user.getLoginId());
        Optional<User> notUser = memoryUserRepository.findByLoginId("not");

        assertThat(findUser.orElse(null)).isEqualTo(user);
        assertThat(notUser.isPresent()).isFalse();
    }

}