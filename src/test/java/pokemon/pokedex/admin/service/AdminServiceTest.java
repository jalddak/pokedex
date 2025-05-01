package pokemon.pokedex.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.CheckedUserDTO;
import pokemon.pokedex.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;


    private CheckedUserDTO checkedUserDTO;

    @BeforeEach
    void setUp() {
        Long testUserId = 1L;
        User user = new User();
        user.setId(testUserId);
        userRepository.save(user);

        checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setId(testUserId);
        checkedUserDTO.setAdminRequestStatus(AdminRequestStatus.NONE);
    }

    @Test
    @DisplayName("AdminRequestStatus 값이 잘 바뀌었을 때")
    void changeAdminRequestStatus_success() {
        adminService.changeAdminRequestStatus(checkedUserDTO, AdminRequestStatus.REQUESTED);
    }

    @Test
    @DisplayName("접속한 유저의 AdminRequestStatus가 바꾸려는 값과 같을 때")
    void changeAdminRequestStatus_fail_1() {
        checkedUserDTO.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        assertThatThrownBy(() -> adminService.changeAdminRequestStatus(checkedUserDTO, AdminRequestStatus.REQUESTED))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("비정상적인 요청을 만들어서 없는 id의 값을 바꾸려고 할 때")
    void changeAdminRequestStatus_fail_2() {
        checkedUserDTO.setId(33L);

        assertThatThrownBy(() -> adminService.changeAdminRequestStatus(checkedUserDTO, AdminRequestStatus.REQUESTED))
                .isInstanceOf(IllegalArgumentException.class);
    }
}