package pokemon.pokedex._common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
class HomeControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

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
        sessionUserDTO.setUsername("testUsername");

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("login-home"))
                .andExpect(model().attribute("user", sessionUserDTO));
    }
}