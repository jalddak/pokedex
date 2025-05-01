package pokemon.pokedex.admin.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.dto.CheckedUserDTO;
import pokemon.pokedex.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    @DisplayName("AdminRequestStatus 값이 잘 바뀌었을 때")
    void changeAdminRequestStatus_success() {
        CheckedUserDTO checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setId(33L);
        checkedUserDTO.setAdminRequestStatus(AdminRequestStatus.NONE);

        doReturn(1)
                .when(userRepository)
                .updateAdminRequestStatusById(any(Long.class), any(AdminRequestStatus.class));

        adminService.changeAdminRequestStatus(checkedUserDTO, AdminRequestStatus.REQUESTED);

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).updateAdminRequestStatusById(captor.capture(), any(AdminRequestStatus.class));

        Long usedId = captor.getValue();
        assertThat(usedId).isEqualTo(checkedUserDTO.getId());

    }

    @Test
    @DisplayName("접속한 유저의 AdminRequestStatus가 바꾸려는 값과 같을 때")
    void changeAdminRequestStatus_fail_1() {
        CheckedUserDTO checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setId(33L);
        checkedUserDTO.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        assertThatThrownBy(() -> adminService.changeAdminRequestStatus(checkedUserDTO, AdminRequestStatus.REQUESTED))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository, never()).updateAdminRequestStatusById(any(Long.class), any(AdminRequestStatus.class));
    }

    @Test
    @DisplayName("비정상적인 요청을 만들어서 없는 id의 값을 바꾸려고 할 때")
    void changeAdminRequestStatus_fail_2() {
        CheckedUserDTO checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setId(33L);
        checkedUserDTO.setAdminRequestStatus(AdminRequestStatus.NONE);

        doReturn(0)
                .when(userRepository)
                .updateAdminRequestStatusById(any(Long.class), any(AdminRequestStatus.class));

        assertThatThrownBy(() -> adminService.changeAdminRequestStatus(checkedUserDTO, AdminRequestStatus.REQUESTED))
                .isInstanceOf(IllegalArgumentException.class);

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).updateAdminRequestStatusById(captor.capture(), any(AdminRequestStatus.class));

        Long usedId = captor.getValue();
        assertThat(usedId).isEqualTo(checkedUserDTO.getId());
    }

}