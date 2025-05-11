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
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.repository.UserRepository;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NormalUserOnlyInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("일반 유저 접속")
    void preHandle_normal() throws Exception {
        User user = new User();
        user.setIsDeleted(false);
        user.setRole(Role.NORMAL);
        user.setAdminRequestStatus(AdminRequestStatus.REQUESTED);
        User savedUser = userRepository.save(user);

        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(savedUser.getId());
        sessionUserDTO.setRole(Role.ADMIN);
        sessionUserDTO.setAdminRequestStatus(AdminRequestStatus.NONE);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/alert")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/alert"));
    }

    @Test
    @DisplayName("관리자 접속")
    void preHandle_admin() throws Exception {
        User user = new User();
        user.setIsDeleted(false);
        user.setRole(Role.ADMIN);
        user.setAdminRequestStatus(AdminRequestStatus.APPROVED);
        User savedUser = userRepository.save(user);

        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(savedUser.getId());
        sessionUserDTO.setRole(Role.NORMAL);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/alert")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

    }
}