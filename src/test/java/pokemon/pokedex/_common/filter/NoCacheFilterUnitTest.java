package pokemon.pokedex._common.filter;

import jakarta.servlet.Filter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pokemon.pokedex._common.HomeController;
import pokemon.pokedex._global.WebConfig;
import pokemon.pokedex.user.controller.LoginController;
import pokemon.pokedex.user.controller.RegisterController;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.exception.LoginFailedException;
import pokemon.pokedex.user.service.LoginService;
import pokemon.pokedex.user.service.RegisterService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LoginController.class, RegisterController.class, HomeController.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class}))
class NoCacheFilterUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private RegisterService registerService;

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
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLoginId("testAdmin");
        loginDTO.setPassword("testPassword123");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(MockMvcResultMatchers.header().string("Pragma", "no-cache"));

    }

    @Test
    @DisplayName("POST 필터에 걸리는 경우 (로그인 실패)")
    public void filter_post_login_failed() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLoginId("testAdmin");
        loginDTO.setPassword("testPassword123");

        doThrow(new LoginFailedException("Login Failed")).when(loginService).checkLogin(any(LoginDTO.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(MockMvcResultMatchers.header().string("Pragma", "no-cache"));

    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/assets/monsterball.svg"})
    @DisplayName("GET 필터에 걸리지 않는 경우")
    public void noFilter_get(String path) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(path))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Cache-Control"))
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Pragma"))
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Expires"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/logout"})
    @DisplayName("POST 필터에 걸리지 않는 경우")
    public void noFilter_post(String path) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(path))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Cache-Control"))
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Pragma"))
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Expires"));
    }

    @TestConfiguration
    static class TestConfig implements WebMvcConfigurer {

        @Bean
        public FilterRegistrationBean noCacheFilter() {
            FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
            filterRegistrationBean.setFilter(new NoCacheFilter());
            filterRegistrationBean.addUrlPatterns("/login", "/register");

            return filterRegistrationBean;
        }
    }
}