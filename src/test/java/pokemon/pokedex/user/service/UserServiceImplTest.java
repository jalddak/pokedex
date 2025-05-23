package pokemon.pokedex.user.service;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    @Mock
    private SessionRegistry sessionRegistry;

    @Mock
    private SessionUserDTO sessionUserDTO;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("삭제되지 않은 유저")
    void getRealUserDTO() {
        User user = new User();
        user.setIsDeleted(false);
        doReturn(Optional.ofNullable(user)).when(userRepository).findById(any(Long.class));

        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);

        SessionUserDTO result = userService.getRealUserDTO(sessionUserDTO);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("삭제된 유저")
    void getRealUserDTO_deletedUser() {
        User user = new User();
        user.setIsDeleted(true);
        doReturn(Optional.ofNullable(user)).when(userRepository).findById(any(Long.class));

        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);

        SessionUserDTO result = userService.getRealUserDTO(sessionUserDTO);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("없는 유저")
    void getRealUserDTO_not_existUser() {
        doReturn(Optional.ofNullable(null)).when(userRepository).findById(any(Long.class));

        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);

        SessionUserDTO result = userService.getRealUserDTO(sessionUserDTO);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("requestAdminRole_성공")
    void requestAdminRole_success() {
        Long userId = 1L;
        AdminRequestStatus expectedAdminRequestStatus = AdminRequestStatus.REQUESTED;

        doReturn(userId).when(sessionUserDTO).getId();
        doReturn(AdminRequestStatus.NONE).when(sessionUserDTO).getAdminRequestStatus();
        doReturn(1).when(userRepository).updateAdminRequestStatusById(any(Long.class), any(AdminRequestStatus.class));
        doReturn(List.of(session)).when(sessionRegistry).getSessionsByUserId(any(Long.class));
        doReturn(sessionUserDTO).when(session).getAttribute(any(String.class));

        userService.requestAdminRole(sessionUserDTO);

        doReturn(AdminRequestStatus.REJECTED).when(sessionUserDTO).getAdminRequestStatus();
        userService.requestAdminRole(sessionUserDTO);

        verify(userRepository, times(2)).updateAdminRequestStatusById(userId, expectedAdminRequestStatus);
        verify(sessionRegistry, times(2)).getSessionsByUserId(userId);
        verify(session, times(2)).getAttribute(SessionConst.SESSION_USER_DTO);
        verify(sessionUserDTO, times(2)).setAdminRequestStatus(expectedAdminRequestStatus);
        verify(session, times(2)).setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO);
    }

    @Test
    @DisplayName("requestAdminRole_안함_AdminRequestStatus가 APPROVE나 REQUESTED 일때")
    void requestAdminRole_nothing() {
        Long userId = 1L;
        AdminRequestStatus expectedAdminRequestStatus = AdminRequestStatus.REQUESTED;

        doReturn(userId).when(sessionUserDTO).getId();
        doReturn(AdminRequestStatus.REQUESTED).when(sessionUserDTO).getAdminRequestStatus();
        
        userService.requestAdminRole(sessionUserDTO);
        doReturn(AdminRequestStatus.APPROVED).when(sessionUserDTO).getAdminRequestStatus();
        userService.requestAdminRole(sessionUserDTO);

        verify(userRepository, never()).updateAdminRequestStatusById(userId, expectedAdminRequestStatus);
        verify(sessionRegistry, never()).getSessionsByUserId(userId);
    }
}