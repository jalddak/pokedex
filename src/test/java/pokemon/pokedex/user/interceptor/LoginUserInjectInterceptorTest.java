package pokemon.pokedex.user.interceptor;

import org.hamcrest.Matchers;
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
import pokemon.pokedex.user.service.UserService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginUserInjectInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("GET 로그인 유저 접근")
    void loginUser() throws Exception {
        User user = new User();
        user.setIsDeleted(false);
        user.setRole(Role.ADMIN);
        User savedUser = userRepository.save(user);

        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(savedUser.getId());
        sessionUserDTO.setLoginId("testLoginId");
        sessionUserDTO.setUsername("testUsername");
        sessionUserDTO.setRole(Role.ADMIN);
        sessionUserDTO.setAdminRequestStatus(AdminRequestStatus.APPROVED);

        SessionUserDTO expectedSessionUserDTO = userService.getRealUserDTO(sessionUserDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"))
                .andExpect(request().attribute(SessionConst.SESSION_USER_DTO,
                        Matchers.samePropertyValuesAs(expectedSessionUserDTO)))
                .andExpect(model().attribute("user",
                        Matchers.samePropertyValuesAs(expectedSessionUserDTO)));
    }

    @Test
    @DisplayName("GET 비로그인 유저 접근 - 모델에 user 없음")
    void guestUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("user"));


        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .sessionAttr("temp", "value"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("user"));
    }
}