package pokemon.pokedex.user.interceptor;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex.ClearMemory;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.RegisterResponseDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.repository.UserRepository;
import pokemon.pokedex.user.service.UserService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginCheckInterceptorTest extends ClearMemory {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("GET 로그인 유저 접근")
    void loginUser_get() throws Exception {

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
                .andExpect(request().sessionAttribute(SessionConst.SESSION_USER_DTO,
                        Matchers.samePropertyValuesAs(expectedSessionUserDTO)))
                .andExpect(request().attribute(SessionConst.SESSION_USER_DTO,
                        Matchers.samePropertyValuesAs(expectedSessionUserDTO)));
        ;
    }

    @Test
    @DisplayName("GET 삭제된 유저 접근")
    void deletedUser_get() throws Exception {
        User user = new User();
        user.setIsDeleted(true);
        user.setRole(Role.ADMIN);
        User savedUser = userRepository.save(user);

        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(savedUser.getId());
        sessionUserDTO.setLoginId("testLoginId");
        sessionUserDTO.setUsername("testUsername");
        sessionUserDTO.setRole(Role.NORMAL);
        sessionUserDTO.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirectURI=/admin"));
    }

    @Test
    @DisplayName("GET 비로그인(게스트) 유저 접근")
    void guest_get() throws Exception {
        RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO();
        registerResponseDTO.setId(1L);
        registerResponseDTO.setUsername("testUsername");

        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirectURI=/admin"));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .sessionAttr(SessionConst.REGISTER_RESPONSE_DTO, registerResponseDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirectURI=/admin"));
    }
}