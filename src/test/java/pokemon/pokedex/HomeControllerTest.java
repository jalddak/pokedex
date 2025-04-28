package pokemon.pokedex;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex.user.dto.LoginResponseDTO;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

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
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setUsername("testUsername");

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .sessionAttr(SessionConst.LOGIN_RESPONSE_DTO, loginResponseDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("loginHome"))
                .andExpect(model().attribute("user", loginResponseDTO));
    }

}