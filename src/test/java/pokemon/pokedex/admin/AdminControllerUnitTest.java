package pokemon.pokedex.admin;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.admin.service.AdminService;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.CheckedUserDTO;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {AdminConfig.class}))
class AdminControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Test
    @DisplayName("GET 관리자 홈")
    void home() throws Exception {
        CheckedUserDTO checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setRole(Role.NORMAL);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .flashAttr("user", checkedUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"));
    }

    @Test
    @DisplayName("POST 관리자 페이지에서 로그아웃")
    void logout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    @DisplayName("GET 관리자 페이지 경고 페이지")
    void alert() throws Exception {
        CheckedUserDTO checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setAdminRequestStatus(AdminRequestStatus.NONE);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/alert")
                        .flashAttr("user", checkedUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/alert"));
    }

    @Test
    @DisplayName("POST 관리자 권한 요청 제출")
    void requestAdminRole() throws Exception {
        CheckedUserDTO checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setAdminRequestStatus(AdminRequestStatus.NONE);

        CheckedUserDTO expectedCheckedUserDTO = new CheckedUserDTO();
        expectedCheckedUserDTO.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        doNothing().when(adminService)
                .changeAdminRequestStatus(any(CheckedUserDTO.class), any(AdminRequestStatus.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/alert")
                        .flashAttr("user", expectedCheckedUserDTO)
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, checkedUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/alert"))
                .andExpect(request().sessionAttribute(SessionConst.CHECKED_USER_DTO,
                        Matchers.samePropertyValuesAs(expectedCheckedUserDTO)));
    }
}