package pokemon.pokedex.user.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex._global.WebConfig;
import pokemon.pokedex.user.controller.LoginController;
import pokemon.pokedex.user.controller.RegisterController;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.LoginService;
import pokemon.pokedex.user.service.RegisterService;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {LoginController.class, RegisterController.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class}))
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
    @DisplayName("GET 게스트 접근")
    void guest_get(String url, String form) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andExpect(view().name(form));
    }

    @Test
    @DisplayName("POST 게스트 접근")
    public void guest_post() throws Exception {
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
    @ValueSource(strings = {"/login", "/register"})
    @DisplayName("POST 유저 접근")
    public void loginUser_post(String url) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .sessionAttr(SessionConst.SESSION_USER_DTO, new SessionUserDTO()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        ;
    }

    @TestConfiguration
    static class GuestOnlyInterceptorConfiguration implements WebMvcConfigurer {

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new GuestOnlyInterceptor())
                    .addPathPatterns("/login", "/register");
        }
    }

}