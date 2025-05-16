package pokemon.pokedex.admin.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pokemon.pokedex.__testutils.WebMvcTestWithExclude;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.admin.AdminController;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.UserService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTestWithExclude(AdminController.class)
class AdminCheckInterceptorUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AdminController(userService))
                .addInterceptors(new AdminCheckInterceptor())
                .build();
    }

    @Test
    @DisplayName("일반 유저 접속")
    void preHandle_normal() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setRole(Role.NORMAL);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .requestAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/alert"));

    }

    @Test
    @DisplayName("관리자 접속")
    void preHandle_admin() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setRole(Role.ADMIN);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .requestAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"));

    }
}