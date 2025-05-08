package pokemon.pokedex.user.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex.admin.AdminController;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.RegisterResponseDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@ExtendWith(MockitoExtension.class)
class LoginCheckInterceptorUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AdminController(userService))
                .addInterceptors(new LoginCheckInterceptor(userService))
                .build();
    }

    @Test
    @DisplayName("GET 로그인 유저 접근")
    void loginUser_get() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);
        sessionUserDTO.setLoginId("testLoginId");
        sessionUserDTO.setUsername("testUsername");
        sessionUserDTO.setRole(Role.NORMAL);
        sessionUserDTO.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        SessionUserDTO realUserDTO = new SessionUserDTO();
        realUserDTO.setId(1L);
        realUserDTO.setLoginId("realLoginId");
        realUserDTO.setUsername("realUsername");
        realUserDTO.setRole(Role.ADMIN);
        realUserDTO.setAdminRequestStatus(AdminRequestStatus.APPROVED);

        doReturn(realUserDTO).when(userService).getRealUserDTO(any(SessionUserDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .session(new MockHttpSession())
                        .requestAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"))
                .andExpect(request().sessionAttribute(SessionConst.SESSION_USER_DTO, realUserDTO))
                .andExpect(request().attribute(SessionConst.SESSION_USER_DTO, realUserDTO));
    }

    @Test
    @DisplayName("GET 삭제된 유저 접근")
    void deletedUser_get() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);
        sessionUserDTO.setLoginId("testLoginId");
        sessionUserDTO.setUsername("testUsername");
        sessionUserDTO.setRole(Role.NORMAL);
        sessionUserDTO.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        doReturn(null).when(userService).getRealUserDTO(any(SessionUserDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .session(new MockHttpSession())
                        .requestAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirectURI=/admin"));
    }

    @Test
    @DisplayName("GET 비로그인(게스트) 유저 접근")
    void guest_get() throws Exception {
        RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO();
        registerResponseDTO.setId(1L);
        registerResponseDTO.setUsername("testUsername");

        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirectURI=/admin"));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .requestAttr(SessionConst.REGISTER_RESPONSE_DTO, registerResponseDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirectURI=/admin"));
    }
}