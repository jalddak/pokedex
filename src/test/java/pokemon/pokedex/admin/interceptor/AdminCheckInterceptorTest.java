package pokemon.pokedex.admin.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.CheckedUserDTO;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminCheckInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("일반 유저 접근")
    void accessNormalUser() throws Exception {
        CheckedUserDTO checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setRole(Role.NORMAL);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, checkedUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/alert"));
    }

    @Test
    @DisplayName("관리자 접근")
    void accessAdmin() throws Exception {
        CheckedUserDTO checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setRole(Role.ADMIN);
        checkedUserDTO.setUsername("admin");

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, checkedUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"));
    }
}
