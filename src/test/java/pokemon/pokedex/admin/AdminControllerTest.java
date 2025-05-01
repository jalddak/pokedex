package pokemon.pokedex.admin;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.admin.service.AdminService;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.CheckedUserDTO;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminService adminService;

    private CheckedUserDTO checkedUserDTO;

    @BeforeEach
    void setUp() {
        checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setId(1L);
        checkedUserDTO.setLoginId("testLoginId");
        checkedUserDTO.setUsername("testUsername");
        checkedUserDTO.setRole(Role.ADMIN);
        checkedUserDTO.setAdminRequestStatus(AdminRequestStatus.APPROVED);
    }

    @Test
    @DisplayName("GET 관리자 홈")
    void home() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, checkedUserDTO))
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
        checkedUserDTO.setRole(Role.NORMAL);
        checkedUserDTO.setAdminRequestStatus(AdminRequestStatus.NONE);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/alert")
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, checkedUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/alert"));
    }

    @Test
    @DisplayName("POST 관리자 권한 요청 제출")
    void requestAdminRole() throws Exception {
        checkedUserDTO.setRole(Role.NORMAL);
        checkedUserDTO.setAdminRequestStatus(AdminRequestStatus.NONE);

        CheckedUserDTO expectedCheckedUserDTO = new CheckedUserDTO();

        expectedCheckedUserDTO.setId(1L);
        expectedCheckedUserDTO.setLoginId("testLoginId");
        expectedCheckedUserDTO.setUsername("testUsername");
        expectedCheckedUserDTO.setRole(Role.NORMAL);
        expectedCheckedUserDTO.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/alert")
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, checkedUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/alert"))
                .andExpect(request().sessionAttribute(SessionConst.CHECKED_USER_DTO,
                        Matchers.samePropertyValuesAs(expectedCheckedUserDTO)));
    }
}