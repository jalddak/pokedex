package pokemon.pokedex.user.controller;

import jakarta.servlet.http.HttpSession;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex.ClearMemory;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.RegisterResponseDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.repository.UserRepository;
import pokemon.pokedex.user.service.LoginService;
import pokemon.pokedex.user.service.RegisterService;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest extends ClearMemory {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginService loginService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UserRepository userRepository;

    private LoginDTO loginDTO;

    private RegisterResponseDTO registerResponseDTO;

    private static Stream<Arguments> provideErrorInputs() {
        return Stream.of(
                Arguments.of("loginId", null, "NotEmpty"),
                Arguments.of("loginId", "", "NotEmpty"),
                Arguments.of("password", null, "NotEmpty")
        );
    }

    @BeforeEach
    void setUp() {
        String testLoginId = "testLoginId";
        String testPassword = "testPassword123";

        loginDTO = new LoginDTO();
        loginDTO.setLoginId(testLoginId);
        loginDTO.setPassword(testPassword);

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("testUsername");
        registerDTO.setLoginId(testLoginId);
        registerDTO.setEmail("testEmail@email.com");
        registerDTO.setPassword(testPassword);
        registerDTO.setConfirmPassword(testPassword);
        registerResponseDTO = registerService.addUser(registerDTO);
    }

    @Test
    @DisplayName("GET 로그인 폼")
    void loginForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login-form"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("POST 로그인 성공")
    void loginSuccess() throws Exception {

        SessionUserDTO expectedSessionUserDTO = loginService.checkLogin(loginDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttribute(SessionConst.SESSION_USER_DTO,
                        Matchers.samePropertyValuesAs(expectedSessionUserDTO)));
    }

    @Test
    @DisplayName("POST 로그인 성공 - 이전 세션 무효화")
    void loginSuccess_before_session_invalidate() throws Exception {

        MockHttpSession session = new MockHttpSession();
        String sessionId = session.getId();

        HttpSession firstSession = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO)
                        .session(session))
                .andReturn().getRequest().getSession(false);
        String firstSessionId = firstSession.getId();

        HttpSession secondSession = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO)
                        .session(session))
                .andReturn().getRequest().getSession(false);
        String secondSessionId = secondSession.getId();

        assertThat(firstSessionId).isNotEqualTo(sessionId);
        assertThat(secondSessionId).isNotEqualTo(sessionId);
        assertThat(secondSessionId).isNotEqualTo(firstSessionId);
    }

    @Test
    @DisplayName("POST 로그인 성공 - redirectURI")
    void loginSuccess_redirectURI() throws Exception {

        String redirectURI = "testRedirectURI";
        SessionUserDTO expectedSessionUserDTO = loginService.checkLogin(loginDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/login?redirectURI=" + redirectURI)
                        .flashAttr("user", loginDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectURI))
                .andExpect(request().sessionAttribute(SessionConst.SESSION_USER_DTO,
                        Matchers.samePropertyValuesAs(expectedSessionUserDTO)));
    }


    @Test
    @DisplayName("로그인 실패 예외처리")
    void loginFail_exception() throws Exception {
        loginDTO.setPassword("wrongPassword");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("login-form"))
                .andExpect(model().attributeHasErrors("user"));

    }

    @ParameterizedTest
    @MethodSource("provideErrorInputs")
    @DisplayName("로그인 실패 에러코드")
    void loginFail_error(String field, String value, String errorCode) throws Exception {
        switch (field) {
            case "loginId" -> loginDTO.setLoginId(value);
            case "password" -> loginDTO.setPassword(value);
        }

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("login-form"))
                .andExpect(model().attributeHasFieldErrorCode("user", field, errorCode));
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logoutSuccess() throws Exception {
        SessionUserDTO sessionUserDTO = loginService.checkLogin(loginDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                        .sessionAttr(SessionConst.REGISTER_RESPONSE_DTO, registerResponseDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

}
