package pokemon.pokedex.admin.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.CheckedUserDTO;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NormalUserOnlyInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

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
}