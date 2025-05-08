package pokemon.pokedex.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex.ClearMemory;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.repository.UserRepository;
import pokemon.pokedex.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest extends ClearMemory {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private SessionUserDTO sessionUserDTO;
    private SessionUserDTO sessionUserDTO2;

    @BeforeEach
    void setUp() {
        String loginId = "testLoginId";
        String password = "testPassword123";

        User user = new User();
        user.setLoginId(loginId);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(password));
        user.setRole(Role.ADMIN);
        user.setIsDeleted(false);
        User savedAdmin = userRepository.save(user);

        sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setLoginId(loginId);
        sessionUserDTO.setRole(Role.ADMIN);
        sessionUserDTO.setId(savedAdmin.getId());

        User user2 = new User();
        user2.setLoginId(loginId);
        user2.setPassword(encoder.encode(password));
        user2.setRole(Role.NORMAL);
        user2.setIsDeleted(false);
        user2.setAdminRequestStatus(AdminRequestStatus.NONE);
        User savedUser = userRepository.save(user2);

        sessionUserDTO2 = new SessionUserDTO();
        sessionUserDTO2.setLoginId(loginId);
        sessionUserDTO2.setRole(Role.NORMAL);
        sessionUserDTO2.setId(savedUser.getId());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO2);

        sessionRegistry.addSession(savedUser.getId(), session);
    }

    @Test
    void home() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"));
    }

    @Test
    void logout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void alert() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/alert")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO2))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/alert"));
    }

    @Test
    void requestAdminRole() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/alert")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO2))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/alert"));

        SessionUserDTO sessionUserDTOTemp = (SessionUserDTO) sessionRegistry.getSessionsByUserId(sessionUserDTO2.getId()).get(0)
                .getAttribute(SessionConst.SESSION_USER_DTO);
        assertThat(sessionUserDTOTemp.getAdminRequestStatus()).isEqualTo(AdminRequestStatus.REQUESTED);
        assertThat(userRepository.findById(sessionUserDTO2.getId()).get().getAdminRequestStatus())
                .isEqualTo(AdminRequestStatus.REQUESTED);

    }
}