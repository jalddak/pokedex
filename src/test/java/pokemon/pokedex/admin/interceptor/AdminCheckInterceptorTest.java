package pokemon.pokedex.admin.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.LoginService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pokemon.pokedex.__testutils.TestDataFactory.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminCheckInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginService loginService;

    @Test
    @DisplayName("일반 유저 접속")
    void preHandle_normal() throws Exception {
        SessionUserDTO sessionUserDTO = loginService.checkLogin(createLoginDTO(defaultInfos));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/alert"));

    }

    @Test
    @DisplayName("관리자 접속")
    void preHandle_admin() throws Exception {
        SessionUserDTO sessionUserDTO = loginService.checkLogin(createLoginDTO(adminInfos));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"));

    }
}