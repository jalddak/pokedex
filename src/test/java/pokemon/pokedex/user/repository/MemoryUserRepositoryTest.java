package pokemon.pokedex.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryUserRepositoryTest {

    private MemoryUserRepository memoryUserRepository;

    @BeforeEach
    void setUp() {
        memoryUserRepository = new MemoryUserRepository();
        memoryUserRepository.clear();
    }

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

        System.out.println(memoryUserRepository.findAll().size());

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

    @Test
    @DisplayName("아이디로 AdminRequestStatus 변경하기")
    void updateAdminRequestStatusById() {
        User user = new User();
        user.setLoginId("user");
        user.setAdminRequestStatus(AdminRequestStatus.NONE);

        User savedUser = memoryUserRepository.save(user);

        int updateCnt = memoryUserRepository.updateAdminRequestStatusById(savedUser.getId(), AdminRequestStatus.REQUESTED);
        assertThat(updateCnt).isEqualTo(1);

        Optional<User> findUser = memoryUserRepository.findById(savedUser.getId());
        assertThat(findUser.isPresent()).isTrue();
        assertThat(findUser.get().getAdminRequestStatus()).isEqualTo(AdminRequestStatus.REQUESTED);
    }

    @Test
    @DisplayName("이미 설정하려는 AdminRequestStatus 상태일때")
    void updateAdminRequestStatusById_already() {
        User user = new User();
        user.setLoginId("user");
        user.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        User savedUser = memoryUserRepository.save(user);

        int updateCnt = memoryUserRepository.updateAdminRequestStatusById(savedUser.getId(), AdminRequestStatus.REQUESTED);
        assertThat(updateCnt).isEqualTo(0);
    }
}