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
import pokemon.pokedex.__testutils.ClearMemory;
import pokemon.pokedex.__testutils.TestDataFactory;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.LoginService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static pokemon.pokedex.__testutils.TestDataFactory.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SessionRegistryTest {

    @Autowired
    private ClearMemory clearMemory;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private LoginService loginService;

    private LoginDTO adminLoginDTO;
    private LoginDTO adminRequestLoginDTO;
    private LoginDTO defaultLoginDTO;

    @BeforeEach
    void setUp() {
        clearMemory.clearMemory();
        adminLoginDTO = createLoginDTO(adminInfos);
        adminRequestLoginDTO = createLoginDTO(TestDataFactory.adminRequestInfos);
        defaultLoginDTO = createLoginDTO(defaultInfos);
    }

    @Test
    @DisplayName("유저 한명 로그인")
    void addSession() {

        SessionUserDTO sessionUserDTO = loginService.checkLogin(defaultLoginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", defaultLoginDTO.getLoginId());
        formData.add("password", defaultLoginDTO.getPassword());

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

        SessionUserDTO sessionUserDTO = loginService.checkLogin(defaultLoginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", defaultLoginDTO.getLoginId());
        formData.add("password", defaultLoginDTO.getPassword());

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

        SessionUserDTO adminSessionUserDTO = loginService.checkLogin(adminLoginDTO);
        SessionUserDTO adminRequestSessionUserDTO = loginService.checkLogin(adminRequestLoginDTO);
        SessionUserDTO defaultSessionUserDTO = loginService.checkLogin(defaultLoginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", adminLoginDTO.getLoginId());
        formData.add("password", adminLoginDTO.getPassword());
        webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange();

        MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
        formData2.add("loginId", adminRequestLoginDTO.getLoginId());
        formData2.add("password", adminRequestLoginDTO.getPassword());
        webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData2)
                .exchange();

        MultiValueMap<String, String> formData3 = new LinkedMultiValueMap<>();
        formData3.add("loginId", defaultLoginDTO.getLoginId());
        formData3.add("password", defaultLoginDTO.getPassword());
        webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData3)
                .exchange();

        List<HttpSession> sessionsByUserId = sessionRegistry.getSessionsByUserId(adminSessionUserDTO.getId());
        assertThat(sessionsByUserId.size()).isEqualTo(1);

        List<HttpSession> sessionsByUserId2 = sessionRegistry.getSessionsByUserId(adminRequestSessionUserDTO.getId());
        assertThat(sessionsByUserId2.size()).isEqualTo(1);

        List<HttpSession> sessionsByUserId3 = sessionRegistry.getSessionsByUserId(defaultSessionUserDTO.getId());
        assertThat(sessionsByUserId3.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("유저 로그아웃")
    void removeSession() {

        SessionUserDTO sessionUserDTO = loginService.checkLogin(defaultLoginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", defaultLoginDTO.getLoginId());
        formData.add("password", defaultLoginDTO.getPassword());

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
                .cookie("JSESSIONID", sessionId)
                .exchange();

        List<HttpSession> afterLogoutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(afterLogoutSessions.size()).isEqualTo(0);

    }

    @Test
    @DisplayName("세션 타임아웃")
    void timeoutSession() throws InterruptedException {

        SessionUserDTO sessionUserDTO = loginService.checkLogin(defaultLoginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", defaultLoginDTO.getLoginId());
        formData.add("password", defaultLoginDTO.getPassword());

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

        // 타임아웃 설정을 해주고 sleep으로 기다린다.
        beforeTimeoutSessions.get(0).setMaxInactiveInterval(1);
        log.debug("sessionTimeout: {}s", beforeTimeoutSessions.get(0).getMaxInactiveInterval());
        TimeUnit.SECONDS.sleep(2);

        // 요청을 한 번 더 불러오지 않는다면, 내장 서버에서 타임아웃을 체크하는 백그라운드 시간이 될 때까지 타임아웃이 된 세션인지 파악이 불가능해서 임시 요청 하나 보냄.
        webTestClient.get()
                .uri("/")
                .cookie("JSESSIONID", sessionId)  // 요청할 URL
                .exchange();

        List<HttpSession> afterTimeoutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(afterTimeoutSessions.size()).isEqualTo(0);

    }

    @Disabled("백그라운드 타임아웃 기다리는것은 너무 오래걸림. 테스트할때만 주석처리")
    @Test
    @DisplayName("세션 타임아웃, 백그라운드 테스트")
    void timeoutSession_background() throws InterruptedException {

        SessionUserDTO sessionUserDTO = loginService.checkLogin(defaultLoginDTO);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("loginId", defaultLoginDTO.getLoginId());
        formData.add("password", defaultLoginDTO.getPassword());

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

        // tomcat 내장 타임아웃 체크 시간은 60초 간격이라고함.
        // 그리고 appication.yml에 자동 타임아웃 시간은 최소 분단위로 최소가 1분이다.
        // 따라서 테스트 최소 maxInactiveInterval 인 1분으로 application.yml에 설정해둬도
        // 더해서 60초를 기다려야하니, 2분은 기다려야 테스트가 완료됨.
        // 물론 바로 위 처럼 세션 타임아웃 시간을 수동으로 1초로 설정해도 61초는 기다려야함.
        TimeUnit.SECONDS.sleep(beforeTimeoutSessions.get(0).getMaxInactiveInterval() + 60);

        List<HttpSession> afterTimeoutSessions = sessionRegistry.getSessionsByUserId(sessionUserDTO.getId());
        assertThat(afterTimeoutSessions.size()).isEqualTo(0);

    }
}