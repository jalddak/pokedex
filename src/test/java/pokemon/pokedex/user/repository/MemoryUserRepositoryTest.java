package pokemon.pokedex.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pokemon.pokedex.user.domain.AdminRequestStatus;
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

    @Test
    @DisplayName("유저의 AdminRequestStatus 업데이트 성공")
    void updateAdminRequestStatusById_success() {
        User user = new User();
        user.setAdminRequestStatus(AdminRequestStatus.NONE);

        User savedUser = memoryUserRepository.save(user);

        int updateCnt = memoryUserRepository.updateAdminRequestStatusById(savedUser.getId(), AdminRequestStatus.REQUESTED);
        assertThat(updateCnt).isEqualTo(1);

        User findUser = memoryUserRepository.findById(savedUser.getId()).orElse(null);
        assertThat(findUser.getAdminRequestStatus()).isEqualTo(AdminRequestStatus.REQUESTED);
    }

    @Test
    @DisplayName("유저가 없거나 바꾸려는 값이 이미 그 값인 경우 실패")
    void updateAdminRequestStatusById_fail() {
        User user = new User();
        user.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        User savedUser = memoryUserRepository.save(user);

        int noUserUpdateCnt = memoryUserRepository.updateAdminRequestStatusById(-1L, AdminRequestStatus.NONE);
        int alreadyUpdatedCnt = memoryUserRepository.updateAdminRequestStatusById(savedUser.getId(), AdminRequestStatus.REQUESTED);

        assertThat(noUserUpdateCnt).isEqualTo(0);
        assertThat(alreadyUpdatedCnt).isEqualTo(0);
    }

}