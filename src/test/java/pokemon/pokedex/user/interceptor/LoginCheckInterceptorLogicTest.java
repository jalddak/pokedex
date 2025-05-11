package pokemon.pokedex.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginCheckInterceptorLogicTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private Object handler;

    @Mock
    private UserService userService;

    @InjectMocks
    private LoginCheckInterceptor loginCheckInterceptor;

    @Test
    @DisplayName("로그인 유저 접속")
    void preHandle_loginUser() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);
        sessionUserDTO.setLoginId("testLoginId");
        sessionUserDTO.setUsername("testUsername");

        doReturn(sessionUserDTO).when(request).getAttribute(SessionConst.SESSION_USER_DTO);
        doReturn(session).when(request).getSession(false);
        doReturn(sessionUserDTO).when(userService).getRealUserDTO(sessionUserDTO);

        boolean result = loginCheckInterceptor.preHandle(request, response, handler);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("삭제된 유저 접속")
    void preHandle_deletedUser() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);
        sessionUserDTO.setLoginId("testLoginId");
        sessionUserDTO.setUsername("testUsername");

        doReturn(sessionUserDTO).when(request).getAttribute(SessionConst.SESSION_USER_DTO);
        doReturn(session).when(request).getSession(false);
        doReturn(null).when(userService).getRealUserDTO(sessionUserDTO);

        boolean result = loginCheckInterceptor.preHandle(request, response, handler);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("로직 도중 세션 무효화 발생")
    void preHandle_session_invalidate() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);
        sessionUserDTO.setLoginId("testLoginId");
        sessionUserDTO.setUsername("testUsername");

        doReturn(sessionUserDTO).when(request).getAttribute(SessionConst.SESSION_USER_DTO);
        doReturn(null).when(request).getSession(false);

        boolean result = loginCheckInterceptor.preHandle(request, response, handler);

        assertThat(result).isFalse();


        doReturn(session).when(request).getSession(false);
        doReturn(sessionUserDTO).when(userService).getRealUserDTO(sessionUserDTO);
        doThrow(new RuntimeException()).when(session).setAttribute(any(String.class), any(SessionUserDTO.class));

        result = loginCheckInterceptor.preHandle(request, response, handler);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("비로그인(게스트) 사용자 접속 - 세션 없음")
    void preHandle_guest_no_session() throws Exception {
        String redirectURI = "/admin";
        doReturn("").when(request).getContextPath();
        doReturn(redirectURI).when(request).getRequestURI();

        doReturn(null).when(request).getAttribute(SessionConst.SESSION_USER_DTO);

        boolean result = loginCheckInterceptor.preHandle(request, response, handler);

        assertThat(result).isFalse();
        verify(response).sendRedirect("/login?redirectURI=" + redirectURI);
    }

    @Test
    @DisplayName("비로그인(게스트) 사용자 접속 - 로그인 세션 없음")
    void preHandle_guest_no_login() throws Exception {
        String redirectURI = "/admin";
        doReturn("").when(request).getContextPath();
        doReturn(redirectURI).when(request).getRequestURI();

        doReturn(null).when(request).getAttribute(SessionConst.SESSION_USER_DTO);

        boolean result = loginCheckInterceptor.preHandle(request, response, handler);

        assertThat(result).isFalse();
        verify(response).sendRedirect("/login?redirectURI=" + redirectURI);
    }

}