package pokemon.pokedex.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pokemon.pokedex._global.WebConfig;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.UserService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class}))
class AdminControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AdminController(userService))
                .build();
    }

    @Test
    void home() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
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

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/alert"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/alert"));
    }

    @Test
    void requestAdminRole() throws Exception {

        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(123L);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/alert")
                        .requestAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/alert"));

        verify(userService).requestAdminRole(sessionUserDTO.getId());
    }
}