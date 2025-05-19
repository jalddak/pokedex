package pokemon.pokedex.user.interceptor;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.LoginService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pokemon.pokedex.__testutils.TestDataFactory.createLoginDTO;
import static pokemon.pokedex.__testutils.TestDataFactory.defaultInfos;

@SpringBootTest
@AutoConfigureMockMvc
class LoginUserInjectInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginService loginService;

    @Test
    @DisplayName("GET 로그인 유저 접근")
    void loginUser() throws Exception {

        SessionUserDTO sessionUserDTO = loginService.checkLogin(createLoginDTO(defaultInfos));

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("login-home"))
                .andExpect(request().attribute(SessionConst.SESSION_USER_DTO,
                        Matchers.samePropertyValuesAs(sessionUserDTO)))
                .andExpect(model().attribute("user",
                        Matchers.samePropertyValuesAs(sessionUserDTO)));
    }

    @Test
    @DisplayName("GET 비로그인 유저 접근 - 모델에 user 없음")
    void guestUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeDoesNotExist("user"));


        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .sessionAttr("temp", "value"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeDoesNotExist("user"));
    }
}