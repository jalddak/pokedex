package pokemon.pokedex.user.interceptor;

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
public class LoginCheckInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("유저(로그인) 접속")
    void access_user() throws Exception {
        CheckedUserDTO checkedUserDTO = new CheckedUserDTO();
        checkedUserDTO.setUsername("admin");
        checkedUserDTO.setRole(Role.ADMIN);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, checkedUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("게스트(비로그인) 접속")
    void access_guest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .sessionAttr(SessionConst.USERNAME, "admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirectURI=/admin"));


        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirectURI=/admin"));
    }

}
