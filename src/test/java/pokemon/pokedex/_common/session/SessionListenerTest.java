package pokemon.pokedex._common.session;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pokemon.pokedex.ClearMemory;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.LoginService;
import pokemon.pokedex.user.service.RegisterService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SessionListenerTest extends ClearMemory {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private LoginService loginService;

    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        String testLoginId = "testLoginId";
        String testPassword = "testPassword123";

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("testUsername");
        registerDTO.setLoginId(testLoginId);
        registerDTO.setEmail("testEmail@test.com");
        registerDTO.setPassword(testPassword);
        registerDTO.setConfirmPassword(testPassword);
        registerService.addUser(registerDTO);

        loginDTO = new LoginDTO();
        loginDTO.setLoginId(testLoginId);
        loginDTO.setPassword(testPassword);
    }

    @Test
    void listener_test() {
        SessionUserDTO sessionUserDTO = loginService.checkLogin(loginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", loginDTO.getLoginId());
        formData.add("password", loginDTO.getPassword());

        WebTestClient.ResponseSpec response = webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange();

        List<HttpSession> beforeTimeOutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(beforeTimeOutSessions.size()).isEqualTo(1);

        beforeTimeOutSessions.get(0).invalidate();

        List<HttpSession> afterTimeOutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(afterTimeOutSessions.size()).isEqualTo(0);
    }

}