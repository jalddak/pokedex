package pokemon.pokedex.user.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.CheckedUserDTO;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GuestOnlyInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    private static Stream<Arguments> provideArguments() {
        return Stream.of(
                Arguments.of("/login", "login-form"),
                Arguments.of("/register", "register-form")

        );
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    @DisplayName("게스트 접근")
    void guest(String url, String expectedFormName) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andExpect(view().name(expectedFormName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/login", "/register"})
    @DisplayName("로그인 유저 접근")
    void loginUser_get(String url) throws Exception {
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
