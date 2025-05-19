package pokemon.pokedex.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import pokemon.pokedex.__testutils.ClearMemory;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static pokemon.pokedex.__testutils.TestDataFactory.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private ClearMemory clearMemory;
    @Autowired
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        clearMemory.clearMemory();
    }

    @Test
    @DisplayName("삭제되지 않은 유저")
    void getRealUserDTO() {
        SessionUserDTO sessionUserDTO = loginService.checkLogin(createLoginDTO(defaultInfos));

        SessionUserDTO result = userService.getRealUserDTO(sessionUserDTO);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("삭제된 유저")
    void getRealUserDTO_deletedUser() {
        SessionUserDTO sessionUserDTO = loginService.checkLogin(createLoginDTO(defaultInfos));
        userRepository.deleteById(sessionUserDTO.getId());

        SessionUserDTO result = userService.getRealUserDTO(sessionUserDTO);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("없는 유저")
    void getRealUserDTO_not_existUser() {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(-1L);

        SessionUserDTO result = userService.getRealUserDTO(sessionUserDTO);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("requestAdminRole_성공")
    void requestAdminRole_success() {
        AdminRequestStatus expectedAdminRequestStatus = AdminRequestStatus.REQUESTED;

        SessionUserDTO sessionUserDTO = loginService.checkLogin(createLoginDTO(alreadySessionInfos));
        Long userId = sessionUserDTO.getId();

        userService.requestAdminRole(sessionUserDTO);

        assertThat(userRepository.findById(userId).get().getAdminRequestStatus()).isEqualTo(expectedAdminRequestStatus);
        SessionUserDTO findSessionUserDTO = (SessionUserDTO) sessionRegistry.getSessionsByUserId(userId).get(0)
                .getAttribute(SessionConst.SESSION_USER_DTO);
        assertThat(findSessionUserDTO.getAdminRequestStatus()).isEqualTo(expectedAdminRequestStatus);
    }

    @Test
    @DisplayName("requestAdminRole_안함")
    void requestAdminRole_nothing() {
        AdminRequestStatus adminRequestStatus = AdminRequestStatus.REQUESTED;

        SessionUserDTO sessionUserDTO = loginService.checkLogin(createLoginDTO(adminRequestInfos));
        Long userId = sessionUserDTO.getId();

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO);

        sessionRegistry.addSession(userId, session);

        userService.requestAdminRole(sessionUserDTO);

        assertThat(userRepository.findById(userId).get().getAdminRequestStatus()).isEqualTo(adminRequestStatus);
        SessionUserDTO findSessionUserDTO = (SessionUserDTO) sessionRegistry.getSessionsByUserId(userId).get(0)
                .getAttribute(SessionConst.SESSION_USER_DTO);
        assertThat(findSessionUserDTO.getAdminRequestStatus()).isEqualTo(adminRequestStatus);
    }

    @Test
    @DisplayName("requestAdminRole_유저 없으면 아무 변화 없음")
    void requestAdminRole_nothing_not_error() {


        assertThat(sessionRegistry.getSessionsByUserId(-1L)).isEmpty();
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(-1L);
        userService.requestAdminRole(sessionUserDTO);

        assertThat(sessionRegistry.getSessionsByUserId(-1L)).isEmpty();
    }
}