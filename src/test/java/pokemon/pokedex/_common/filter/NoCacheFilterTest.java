package pokemon.pokedex._common.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.service.RegisterService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NoCacheFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegisterService registerService;

    @ParameterizedTest
    @ValueSource(strings = {"/login", "/register"})
    @DisplayName("GET 필터에 걸리는 경우")
    public void filter_get(String path) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(path))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(MockMvcResultMatchers.header().string("Pragma", "no-cache"));
    }

    @Test
    @DisplayName("POST 필터에 걸리는 경우 (로그인 성공)")
    public void filter_post_login_success() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setLoginId("testAdmin");
        registerDTO.setUsername("testUsername");
        registerDTO.setEmail("testEmail@email.com");
        registerDTO.setPassword("testPassword123");
        registerDTO.setConfirmPassword("testPassword123");
        registerService.addUser(registerDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLoginId("testAdmin");
        loginDTO.setPassword("testPassword123");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(MockMvcResultMatchers.header().string("Pragma", "no-cache"));
    }

    @Test
    @DisplayName("POST 필터에 걸리는 경우 (로그인 실패)")
    public void filter_post_login_failed() throws Exception {

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLoginId("testAdmin");
        loginDTO.setPassword("testPassword123");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(MockMvcResultMatchers.header().string("Pragma", "no-cache"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/assets/monsterball.svg"})
    @DisplayName("GET 필터에 걸리지 않는 경우")
    public void noFilter_get(String path) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(path))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Cache-Control"))
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Pragma"))
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Expires"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/logout"})
    @DisplayName("POST 필터에 걸리지 않는 경우")
    public void noFilter_post(String path) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(path))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Cache-Control"))
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Pragma"))
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Expires"));
    }
}
