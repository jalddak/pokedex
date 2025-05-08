package pokemon.pokedex._common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pokemon.pokedex._global.WebConfig;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = HomeController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class}))
class HomeControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new HomeController())
                .build();
    }

    @Test
    @DisplayName("일반 홈")
    void home() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    @DisplayName("로그인 홈")
    void loginHome() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);
        sessionUserDTO.setLoginId("testLoginId");
        sessionUserDTO.setUsername("testUsername");

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .requestAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("login-home"));
    }
}