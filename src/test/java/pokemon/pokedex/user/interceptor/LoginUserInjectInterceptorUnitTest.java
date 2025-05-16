package pokemon.pokedex.user.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pokemon.pokedex.__testutils.WebMvcTestWithExclude;
import pokemon.pokedex._common.HomeController;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTestWithExclude(HomeController.class)
class LoginUserInjectInterceptorUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new HomeController())
                .addInterceptors(new LoginUserInjectInterceptor())
                .build();
    }

    @Test
    @DisplayName("GET 로그인 유저 접근")
    void loginUser() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("login-home"))
                .andExpect(request().attribute(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(model().attribute("user", sessionUserDTO));
    }

    @Test
    @DisplayName("GET 비로그인 유저 접근 - 모델에 user 없음")
    void guestUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("user"));

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .sessionAttr("temp", "value"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("user"));
    }
}