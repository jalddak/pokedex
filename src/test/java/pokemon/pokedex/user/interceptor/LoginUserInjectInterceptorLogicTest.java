package pokemon.pokedex.user.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;

import static org.assertj.core.api.Assertions.assertThat;

class LoginUserInjectInterceptorLogicTest {

    private LoginUserInjectInterceptor loginUserInjectInterceptor;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;
    private ModelAndView modelAndView;

    @BeforeEach
    void setUp() {
        loginUserInjectInterceptor = new LoginUserInjectInterceptor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        modelAndView = new ModelAndView();
    }

    @Test
    @DisplayName("preHandle 로그인 유저 접근 - request에 유저 정보 주입")
    void preHandle_loginUser() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();

        session.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO);
        request.setSession(session);

        boolean result = loginUserInjectInterceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        assertThat(request.getAttribute(SessionConst.SESSION_USER_DTO)).isEqualTo(sessionUserDTO);
    }

    @Test
    @DisplayName("preHandle 게스트 접근 - 유저 주입 없음 - 세션 없음")
    void preHandle_guest_no_session() throws Exception {
        boolean result = loginUserInjectInterceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        assertThat(request.getAttribute(SessionConst.SESSION_USER_DTO)).isNull();

    }

    @Test
    @DisplayName("preHandle 게스트 접근 - 유저 주입 없음 - 로그인 세션 없음")
    void preHandle_guest_no_loginSession() throws Exception {
        session.setAttribute("temp", "value");
        request.setSession(session);

        boolean result = loginUserInjectInterceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        assertThat(request.getAttribute(SessionConst.SESSION_USER_DTO)).isNull();

    }

    @Test
    @DisplayName("preHandle 로그인 유저 접근")
    void postHandle_loginUser() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();

        session.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO);
        request.setSession(session);

        loginUserInjectInterceptor.preHandle(request, response, new Object());
        loginUserInjectInterceptor.postHandle(request, response, new Object(), modelAndView);
        assertThat(modelAndView.getModel()).containsEntry("user", sessionUserDTO);
    }

    @Test
    @DisplayName("postHandle 게스트 접근 - 유저 주입 없음 - 세션 없음")
    void postHandle_guest_no_session() throws Exception {

        loginUserInjectInterceptor.postHandle(request, response, new Object(), modelAndView);
        assertThat(modelAndView.getModel()).doesNotContainKey("user");
    }

    @Test
    @DisplayName("postHandle 게스트 접근 - 유저 주입 없음 - 로그인 세션 없음")
    void postHandle_guest_no_loginSession() throws Exception {

        session.setAttribute("temp", "value");
        request.setSession(session);

        loginUserInjectInterceptor.preHandle(request, response, new Object());
        loginUserInjectInterceptor.postHandle(request, response, new Object(), modelAndView);
        assertThat(modelAndView.getModel()).doesNotContainKey("user");
    }

}