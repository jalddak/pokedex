package pokemon.pokedex.admin.interceptor;

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
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.admin.AdminConfig;
import pokemon.pokedex.admin.AdminController;
import pokemon.pokedex.admin.service.AdminService;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.CheckedUserDTO;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {AdminConfig.class}))
class NormalUserOnlyInterceptorUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Test
    @DisplayName("관리자 유저 접근")
    void accessAdmin() throws Exception {
        CheckedUserDTO checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setRole(Role.ADMIN);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/alert")
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, checkedUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    @DisplayName("일반 유저 접근")
    void accessNormal() throws Exception {
        CheckedUserDTO checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setRole(Role.NORMAL);
        checkedUserDTO.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/alert")
                        .flashAttr("user", checkedUserDTO)
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, checkedUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/alert"))
                .andExpect(model().attributeExists("user"));
    }

    @TestConfiguration
    public static class TestConfig implements WebMvcConfigurer {

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new NormalUserOnlyInterceptor())
                    .addPathPatterns("/admin/alert");
        }
    }
}