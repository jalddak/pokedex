package pokemon.pokedex._common.session;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pokemon.pokedex.__testutils.ClearMemory;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.LoginService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pokemon.pokedex.__testutils.TestDataFactory.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SessionListenerTest {

    @Autowired
    private ClearMemory clearMemory;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        clearMemory.clearMemory();
    }

    @Test
    void listener_test() {
        LoginDTO loginDTO = createLoginDTO(defaultInfos);
        SessionUserDTO sessionUserDTO = loginService.checkLogin(loginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", loginDTO.getLoginId());
        formData.add("password", loginDTO.getPassword());

        WebTestClient.ResponseSpec response = webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection();

        List<HttpSession> beforeTimeOutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(beforeTimeOutSessions.size()).isEqualTo(1);

        // 세션 무효화
        beforeTimeOutSessions.get(0).invalidate();

        List<HttpSession> afterTimeOutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(afterTimeOutSessions.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("로그인 세션과 관련없는 세션도 무효화시 리스너 불러오는지 확인")
    void test_another_session() throws Exception {
        RegisterDTO registerDTO = createRegisterDTO(registerInfos);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", registerDTO.getLoginId());
        formData.add("username", registerDTO.getUsername());
        formData.add("email", registerDTO.getEmail());
        formData.add("password", registerDTO.getPassword());
        formData.add("confirmPassword", registerDTO.getConfirmPassword());

        WebTestClient.ResponseSpec response = webTestClient.post().uri("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection();

        // "Set-Cookie" 헤더에서 모든 쿠키를 추출
        List<String> cookies = response.returnResult(String.class)
                .getResponseHeaders()
                .get(HttpHeaders.SET_COOKIE);

        // JSESSIONID를 포함하는 쿠키만 추출
        String sessionId = cookies.stream()
                .filter(cookie -> cookie.contains("JSESSIONID"))
                .findFirst()
                .map(cookie -> cookie.split(";")[0].split("=")[1]) // "JSESSIONID=값"
                .orElseThrow(() -> new IllegalStateException("JSESSIONID not found in Set-Cookie"));

        Long userId = loginService.checkLogin(createLoginDTO(registerInfos)).getId();
        webTestClient.get().uri("/register/success/{userId}", userId)
                .cookie("JSESSIONID", sessionId)
                .exchange()
                .expectStatus().isOk();


    }

}