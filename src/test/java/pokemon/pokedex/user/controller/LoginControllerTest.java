package pokemon.pokedex.user.controller;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.CheckedUserDTO;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.repository.MemoryUserRepository;
import pokemon.pokedex.user.repository.UserRepository;
import pokemon.pokedex.user.service.LoginService;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    private LoginDTO loginDTO;

    private static Stream<Arguments> provideErrorInputs() {
        return Stream.of(
                Arguments.of("loginId", null, "NotEmpty"),
                Arguments.of("loginId", "", "NotEmpty"),
                Arguments.of("password", null, "NotEmpty")
        );
    }

    @BeforeEach
    void setUp() {
        if (userRepository instanceof MemoryUserRepository) {
            ((MemoryUserRepository) userRepository).clear();
        }
        loginDTO = new LoginDTO();
        loginDTO.setLoginId("testLoginId");
        loginDTO.setPassword("testPassword123");
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
        User user = new User();
        user.setLoginId(loginDTO.getLoginId());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(loginDTO.getPassword()));

        userRepository.save(user);

        CheckedUserDTO expectedCheckedUserDTO = loginService.checkLogin(loginDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttribute(SessionConst.CHECKED_USER_DTO,
                        Matchers.samePropertyValuesAs(expectedCheckedUserDTO)));
    }

    @Test
    @DisplayName("로그인 성공 - redirectURI")
    void loginRedirectURI() throws Exception {
        User user = new User();
        user.setLoginId(loginDTO.getLoginId());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(loginDTO.getPassword()));

        userRepository.save(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/login?redirectURI=/admin")
                        .flashAttr("user", loginDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    @DisplayName("로그인 실패 예외처리")
    void loginFail_exception() throws Exception {
        User user = new User();
        user.setLoginId(loginDTO.getLoginId());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode("anotherPassword123"));
        userRepository.save(user);

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
        mockMvc.perform(MockMvcRequestBuilders.post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, "something"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                        .sessionAttr(SessionConst.USERNAME, "something"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

}
