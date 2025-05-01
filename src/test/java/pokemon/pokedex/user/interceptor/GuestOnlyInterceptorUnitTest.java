package pokemon.pokedex.user.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.controller.LoginController;
import pokemon.pokedex.user.controller.RegisterController;
import pokemon.pokedex.user.dto.CheckedUserDTO;
import pokemon.pokedex.user.service.LoginService;
import pokemon.pokedex.user.service.RegisterService;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({LoginController.class, RegisterController.class})
class GuestOnlyInterceptorUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private RegisterService registerService;

    private static Stream<Arguments> provideArguments() {
        return Stream.of(
                Arguments.of("/login", "login-form"),
                Arguments.of("/register", "register-form")
        );
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    @DisplayName("게스트 접근")
    void guest(String url, String form) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andExpect(view().name(form));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/login", "/register"})
    @DisplayName("유저 접근")
    void loginUser(String url) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, new CheckedUserDTO()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, new CheckedUserDTO()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

}