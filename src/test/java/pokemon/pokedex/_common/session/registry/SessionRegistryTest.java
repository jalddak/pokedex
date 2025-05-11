package pokemon.pokedex._common.session.registry;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import pokemon.pokedex.ClearMemory;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.LoginService;
import pokemon.pokedex.user.service.RegisterService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SessionRegistryTest extends ClearMemory {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private LoginService loginService;

    private LoginDTO loginDTO;
    private LoginDTO loginDTO2;
    private LoginDTO loginDTO3;

    @BeforeEach
    void setUp() {
        String testLoginId = "testLoginId";
        String testLoginId2 = "testLoginId2";
        String testLoginId3 = "testLoginId3";
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

        registerDTO.setLoginId(testLoginId2);
        registerService.addUser(registerDTO);
        registerDTO.setLoginId(testLoginId3);
        registerService.addUser(registerDTO);

        loginDTO2 = new LoginDTO();
        loginDTO2.setLoginId(testLoginId2);
        loginDTO2.setPassword(testPassword);

        loginDTO3 = new LoginDTO();
        loginDTO3.setLoginId(testLoginId3);
        loginDTO3.setPassword(testPassword);
    }

    @Test
    @DisplayName("유저 한명 로그인")
    void addSession() {

        SessionUserDTO sessionUserDTO = loginService.checkLogin(loginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", loginDTO.getLoginId());
        formData.add("password", loginDTO.getPassword());

        webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange();

        List<HttpSession> sessionsByUserId = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(sessionsByUserId.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("유저 한명이 여러 브라우저에서 로그인")
    void addSession_many_browser() {

        SessionUserDTO sessionUserDTO = loginService.checkLogin(loginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", loginDTO.getLoginId());
        formData.add("password", loginDTO.getPassword());

        webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange();

        webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange();

        webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange();

        List<HttpSession> sessionsByUserId = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(sessionsByUserId.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("여러 유저 로그인")
    void addSession_many_users() {

        SessionUserDTO sessionUserDTO = loginService.checkLogin(loginDTO);
        SessionUserDTO sessionUserDTO2 = loginService.checkLogin(loginDTO2);
        SessionUserDTO sessionUserDTO3 = loginService.checkLogin(loginDTO3);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", loginDTO.getLoginId());
        formData.add("password", loginDTO.getPassword());
        webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange();

        MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
        formData2.add("loginId", loginDTO2.getLoginId());
        formData2.add("password", loginDTO2.getPassword());
        webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData2)
                .exchange();

        MultiValueMap<String, String> formData3 = new LinkedMultiValueMap<>();
        formData3.add("loginId", loginDTO3.getLoginId());
        formData3.add("password", loginDTO3.getPassword());
        webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData3)
                .exchange();

        List<HttpSession> sessionsByUserId = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(sessionsByUserId.size()).isEqualTo(1);

        List<HttpSession> sessionsByUserId2 = sessionRegistry.getSessionsByUserId(sessionUserDTO2.getId());
        assertThat(sessionsByUserId2.size()).isEqualTo(1);

        List<HttpSession> sessionsByUserId3 = sessionRegistry.getSessionsByUserId(sessionUserDTO3.getId());
        assertThat(sessionsByUserId3.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("유저 로그아웃")
    void removeSession() {

        SessionUserDTO sessionUserDTO = loginService.checkLogin(loginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", loginDTO.getLoginId());
        formData.add("password", loginDTO.getPassword());

        WebTestClient.ResponseSpec response = webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange();

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

        List<HttpSession> beforeLogoutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(beforeLogoutSessions.size()).isEqualTo(1);

        webTestClient.post().uri("/logout")
                .cookie("JSESSIONID", sessionId) // "JSESSIONID"는 일반적인 세션 쿠키 이름
                .exchange();

        List<HttpSession> afterLogoutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(afterLogoutSessions.size()).isEqualTo(0);

    }

    @Test
    @DisplayName("세션 타임아웃")
    void timeoutSession() throws InterruptedException {

        SessionUserDTO sessionUserDTO = loginService.checkLogin(loginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", loginDTO.getLoginId());
        formData.add("password", loginDTO.getPassword());

        WebTestClient.ResponseSpec response = webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange();

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

        List<HttpSession> beforeTimeoutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(beforeTimeoutSessions.size()).isEqualTo(1);

        beforeTimeoutSessions.get(0).setMaxInactiveInterval(1);
        log.debug("sessionTimeout: {}s", beforeTimeoutSessions.get(0).getMaxInactiveInterval());
        TimeUnit.SECONDS.sleep(2);

        webTestClient.get()
                .uri("/home")
                .cookie("JSESSIONID", sessionId)  // 요청할 URL
                .exchange();

        List<HttpSession> afterTimeoutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(afterTimeoutSessions.size()).isEqualTo(0);

    }

    @Disabled("백그라운드 타임아웃 기다리는것은 너무 오래걸림. 테스트할때만 주석처리")
    @Test
    @DisplayName("세션 타임아웃, 백그라운드 테스트")
    void timeoutSession_background() throws InterruptedException {

        SessionUserDTO sessionUserDTO = loginService.checkLogin(loginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", loginDTO.getLoginId());
        formData.add("password", loginDTO.getPassword());

        WebTestClient.ResponseSpec response = webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange();

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

        List<HttpSession> beforeTimeoutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(beforeTimeoutSessions.size()).isEqualTo(1);

        TimeUnit.SECONDS.sleep(beforeTimeoutSessions.get(0).getMaxInactiveInterval() + 60);

        List<HttpSession> afterTimeoutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(afterTimeoutSessions.size()).isEqualTo(0);

    }
}