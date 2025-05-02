package pokemon.pokedex.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GuestOnlyInterceptorLogicTest {

    private GuestOnlyInterceptor guestOnlyInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private Object handler;

    @BeforeEach
    public void setup() {
        guestOnlyInterceptor = new GuestOnlyInterceptor();
    }

    @Test
    @DisplayName("비로그인(게스트) 접근 (노 세션)")
    public void preHandle_guest() throws Exception {
        // 세션이 아예 없을 때
        when(request.getSession(false)).thenReturn(null);

        boolean result1 = guestOnlyInterceptor.preHandle(request, response, handler);

        assertThat(result1).isTrue();

        // 세션에 SessionUserDTO 가 없을 때
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(SessionConst.SESSION_USER_DTO)).thenReturn(null);

        boolean result2 = guestOnlyInterceptor.preHandle(request, response, handler);

        assertThat(result2).isTrue();
    }

    @Test
    @DisplayName("유저 접근")
    public void preHandle_user() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(1L);
        sessionUserDTO.setLoginId("testLoginId");
        sessionUserDTO.setUsername("testUsername");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(SessionConst.SESSION_USER_DTO)).thenReturn(sessionUserDTO);
        when(request.getContextPath()).thenReturn("");

        boolean result = guestOnlyInterceptor.preHandle(request, response, handler);

        assertThat(result).isFalse();
        verify(response).sendRedirect("/");
    }

}
