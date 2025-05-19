package pokemon.pokedex._common.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pokemon.pokedex.__testutils.WebMvcTestWithExclude;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex.user.controller.LoginController;
import pokemon.pokedex.user.controller.RegisterController;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.exception.LoginFailedException;
import pokemon.pokedex.user.service.LoginService;
import pokemon.pokedex.user.service.RegisterService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pokemon.pokedex.__testutils.TestDataFactory.*;

@WebMvcTestWithExclude({LoginController.class, RegisterController.class})
class NoCacheFilterUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private RegisterService registerService;

    @MockitoBean
    private SessionRegistry sessionRegistry;


    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new LoginController(loginService, sessionRegistry),
                        new RegisterController(registerService))
                .addFilters(new NoCacheFilter())
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/login", "/register"})
    @DisplayName("GET 필터에 걸리는 경우")
    public void noCacheFilter(String path) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(path))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(MockMvcResultMatchers.header().string("Pragma", "no-cache"));
    }

    @Test
    @DisplayName("POST 필터에 걸리는 경우 (로그인 성공)")
    public void filter_post_login_success() throws Exception {
        LoginDTO loginDTO = createLoginDTO(defaultInfos);

        // WebMvcTest에서는 sessionUserDTO가 뭐가 나오던지 사실 상관없음. 나오기만 하면됨.
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        doReturn(sessionUserDTO).when(loginService).checkLogin(any(LoginDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(MockMvcResultMatchers.header().string("Pragma", "no-cache"));

    }

    @Test
    @DisplayName("POST 필터에 걸리는 경우 (로그인 실패)")
    public void filter_post_login_failed() throws Exception {
        LoginDTO loginDTO = createLoginDTO(registerInfos);

        doThrow(new LoginFailedException("Login Failed")).when(loginService).checkLogin(any(LoginDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(MockMvcResultMatchers.header().string("Pragma", "no-cache"));
    }
}