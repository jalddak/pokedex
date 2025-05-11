package pokemon.pokedex.admin.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pokemon.pokedex._global.WebConfig;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.admin.AdminController;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.interceptor.LoginUserInjectInterceptor;
import pokemon.pokedex.user.service.UserService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class}))
class AdminCheckInterceptorUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AdminController(userService))
                .addInterceptors(new AdminCheckInterceptor())
                .build();
    }

    @Test
    @DisplayName("일반 유저 접속")
    void preHandle_normal() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);
        sessionUserDTO.setLoginId("testLogin");
        sessionUserDTO.setUsername("testNormal");
        sessionUserDTO.setRole(Role.NORMAL);
        sessionUserDTO.setAdminRequestStatus(AdminRequestStatus.NONE);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .requestAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/alert"));

    }

    @Test
    @DisplayName("관리자 접속")
    void preHandle_admin() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);
        sessionUserDTO.setLoginId("testLogin");
        sessionUserDTO.setUsername("testAdmin");
        sessionUserDTO.setRole(Role.ADMIN);
        sessionUserDTO.setAdminRequestStatus(AdminRequestStatus.NONE);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .requestAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"));

    }

    @TestConfiguration
    static class TestConfig implements WebMvcConfigurer {

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new AdminCheckInterceptor())
                    .addPathPatterns("/admin/**")
                    .excludePathPatterns("/admin/logout", "/admin/alert");


            // view에 모델 넣어야돼서 어쩔수 없이 LoginUserInjectInterceptor도 추가함
            registry.addInterceptor(new LoginUserInjectInterceptor())
                    .addPathPatterns("/**")
                    .excludePathPatterns("/assets/**", "/login", "/register", "/logout", "/admin/logout");
        }
    }
}