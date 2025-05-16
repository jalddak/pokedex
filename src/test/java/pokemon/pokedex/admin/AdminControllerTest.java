package pokemon.pokedex.admin;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex.__testutils.ClearMemory;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.LoginService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pokemon.pokedex.__testutils.TestDataFactory.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private ClearMemory clearMemory;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        clearMemory.clearMemory();
    }

    @Test
    void home() throws Exception {
        SessionUserDTO sessionUserDTO = loginService.checkLogin(createLoginDTO(adminInfos));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"));
    }

    @Test
    void logout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andDo(r -> {
                    HttpSession session = r.getRequest().getSession(false);
                    assertThat(session).isNull();
                });
    }

    @Test
    void alert() throws Exception {
        SessionUserDTO sessionUserDTO = loginService.checkLogin(createLoginDTO(defaultInfos));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/alert")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/alert"));
    }

    @Test
    void requestAdminRole() throws Exception {
        SessionUserDTO sessionUserDTO = loginService.checkLogin(createLoginDTO(alreadySessionInfos));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/alert")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/alert"));

    }
}