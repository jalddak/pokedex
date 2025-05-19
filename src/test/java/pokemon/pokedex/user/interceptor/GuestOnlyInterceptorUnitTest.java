package pokemon.pokedex.user.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pokemon.pokedex.__testutils.WebMvcTestWithExclude;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.controller.LoginController;
import pokemon.pokedex.user.controller.RegisterController;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.LoginService;
import pokemon.pokedex.user.service.RegisterService;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTestWithExclude({LoginController.class, RegisterController.class})
class GuestOnlyInterceptorUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private RegisterService registerService;

    @MockitoBean
    private SessionRegistry sessionRegistry;

    private static Stream<Arguments> provideArguments() {
        return Stream.of(
                Arguments.of("/login", "login-form"),
                Arguments.of("/register", "register-form")
        );
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new LoginController(loginService, sessionRegistry),
                        new RegisterController(registerService))
                .addInterceptors(new GuestOnlyInterceptor())
                .build();
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    @DisplayName("GET 게스트 접근")
    void guest_get(String url, String form) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andExpect(view().name(form));
    }

    @Test
    @DisplayName("POST 게스트 접근")
    public void guest_post() throws Exception {
        doReturn(new SessionUserDTO()).when(loginService).checkLogin(any(LoginDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", new LoginDTO()))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/login", "/register"})
    @DisplayName("GET 유저 접근")
    void loginUser_get(String url) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .sessionAttr(SessionConst.SESSION_USER_DTO, new SessionUserDTO()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/login", "/register", "/login?redirectURI=/admin"})
    @DisplayName("POST 유저 접근")
    public void loginUser_post(String url) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .sessionAttr(SessionConst.SESSION_USER_DTO, new SessionUserDTO()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}