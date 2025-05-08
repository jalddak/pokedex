package pokemon.pokedex.user.service;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import pokemon.pokedex.ClearMemory;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex._global.session.registry.SessionRegistry;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest extends ClearMemory {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Test
    @DisplayName("삭제되지 않은 유저")
    void getRealUserDTO() {
        User user = new User();
        user.setIsDeleted(false);
        User savedUser = userRepository.save(user);

        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(savedUser.getId());

        SessionUserDTO result = userService.getRealUserDTO(sessionUserDTO);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("삭제된 유저")
    void getRealUserDTO_deletedUser() {
        User user = new User();
        user.setIsDeleted(true);
        User savedUser = userRepository.save(user);

        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(savedUser.getId());

        SessionUserDTO result = userService.getRealUserDTO(sessionUserDTO);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("없는 유저")
    void getRealUserDTO_not_existUser() {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);

        SessionUserDTO result = userService.getRealUserDTO(sessionUserDTO);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("requestAdminRole_성공")
    void requestAdminRole_success() {
        AdminRequestStatus expectedAdminRequestStatus = AdminRequestStatus.REQUESTED;
        User user = new User();
        user.setAdminRequestStatus(AdminRequestStatus.NONE);
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        HttpSession session = new MockHttpSession();
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(userId);
        sessionUserDTO.setAdminRequestStatus(AdminRequestStatus.NONE);
        session.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO);
        sessionRegistry.addSession(userId, session);

        userService.requestAdminRole(userId);

        assertThat(userRepository.findById(userId).get().getAdminRequestStatus()).isEqualTo(expectedAdminRequestStatus);
        SessionUserDTO findSessionUserDTO = (SessionUserDTO) sessionRegistry.getSessionsByUserId(userId).get(0)
                .getAttribute(SessionConst.SESSION_USER_DTO);
        assertThat(findSessionUserDTO.getAdminRequestStatus()).isEqualTo(expectedAdminRequestStatus);
    }

    @Test
    @DisplayName("requestAdminRole_안함")
    void requestAdminRole_nothing() {
        AdminRequestStatus adminRequestStatus = AdminRequestStatus.REQUESTED;
        User user = new User();
        user.setAdminRequestStatus(adminRequestStatus);
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        HttpSession session = new MockHttpSession();
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(userId);
        sessionUserDTO.setAdminRequestStatus(adminRequestStatus);
        session.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO);
        sessionRegistry.addSession(userId, session);

        userService.requestAdminRole(userId);

        assertThat(userRepository.findById(userId).get().getAdminRequestStatus()).isEqualTo(adminRequestStatus);
        SessionUserDTO findSessionUserDTO = (SessionUserDTO) sessionRegistry.getSessionsByUserId(userId).get(0)
                .getAttribute(SessionConst.SESSION_USER_DTO);
        assertThat(findSessionUserDTO.getAdminRequestStatus()).isEqualTo(adminRequestStatus);
    }

    @Test
    @DisplayName("requestAdminRole_유저 없으면 아무 변화 없음")
    void requestAdminRole_nothing_not_error() {

        assertThat(userRepository.findAll()).isEmpty();
        assertThat(sessionRegistry.getSessionsByUserId(123L)).isEmpty();

        userService.requestAdminRole(123L);
        
        assertThat(userRepository.findAll()).isEmpty();
        assertThat(sessionRegistry.getSessionsByUserId(123L)).isEmpty();
    }
}