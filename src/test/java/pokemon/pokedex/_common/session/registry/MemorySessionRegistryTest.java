package pokemon.pokedex._common.session.registry;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MemorySessionRegistryTest {

    @Mock
    private HttpSession session;

    private MemorySessionRegistry sessionRegistry;

    @BeforeEach
    void setUp() {
        sessionRegistry = new MemorySessionRegistry();
    }

    @Test
    @DisplayName("세션 하나만 추가")
    void addSession() {
        Long userId = 1L;

        sessionRegistry.addSession(userId, session);

        List<HttpSession> sessions = sessionRegistry.getSessionsByUserId(userId);
        assertEquals(1, sessions.size());
        assertEquals(session, sessions.get(0));
    }

    @Test
    @DisplayName("세션 여러개 추가")
    public void addSession_many() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        // 첫 번째 세션 생성
        MockHttpSession session1 = new MockHttpSession();
        SessionUserDTO sessionUserDTO1 = new SessionUserDTO();
        sessionUserDTO1.setId(userId1);
        session1.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO1);

        // 두 번째 세션 생성
        MockHttpSession session2 = new MockHttpSession();
        // 같은 userId로 두 번째 세션을 추가
        SessionUserDTO sessionUserDTO2 = new SessionUserDTO();
        sessionUserDTO2.setId(userId1);
        session2.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO2);

        // 세 번째 세션 생성
        MockHttpSession session3 = new MockHttpSession();
        // 다른 userId로 세 번째 세션을 추가
        SessionUserDTO sessionUserDTO3 = new SessionUserDTO();
        sessionUserDTO3.setId(userId2);
        session3.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO3);

        // 세션들을 MemorySessionRegistry에 추가
        sessionRegistry.addSession(userId1, session1);
        sessionRegistry.addSession(userId1, session2);
        sessionRegistry.addSession(userId2, session3);

        // userId1 의 세션 개수 확인
        List<HttpSession> sessionsForUser1 = sessionRegistry.getSessionsByUserId(1L);
        assertThat(sessionsForUser1).hasSize(2);

        // userId2 의 세션 개수 확인
        List<HttpSession> sessionsForUser2 = sessionRegistry.getSessionsByUserId(2L);
        assertThat(sessionsForUser2).hasSize(1);
    }

    @Test
    void removeSession() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        // 첫 번째 세션 생성
        MockHttpSession session1 = new MockHttpSession();
        SessionUserDTO sessionUserDTO1 = new SessionUserDTO();
        sessionUserDTO1.setId(userId1);
        session1.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO1);

        // 두 번째 세션 생성
        MockHttpSession session2 = new MockHttpSession();
        // 같은 userId로 두 번째 세션을 추가
        SessionUserDTO sessionUserDTO2 = new SessionUserDTO();
        sessionUserDTO2.setId(userId1);
        session2.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO2);

        // 세 번째 세션 생성
        MockHttpSession session3 = new MockHttpSession();
        // 다른 userId로 세 번째 세션을 추가
        SessionUserDTO sessionUserDTO3 = new SessionUserDTO();
        sessionUserDTO3.setId(userId2);
        session3.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO3);

        // 세션들을 MemorySessionRegistry에 추가
        sessionRegistry.addSession(userId1, session1);
        sessionRegistry.addSession(userId1, session2);
        sessionRegistry.addSession(userId2, session3);

        sessionRegistry.removeSession(session1);
        assertThat(sessionRegistry.getSessionsByUserId(userId1)).hasSize(1);
        assertThat(sessionRegistry.getSessionsByUserId(userId1).get(0)).isNotEqualTo(session1);
        assertThat(sessionRegistry.getSessionsByUserId(userId1).get(0)).isEqualTo(session2);

        sessionRegistry.removeSession(session2);
        assertThat(sessionRegistry.getSessionsByUserId(userId1)).isEmpty();
        assertThat(sessionRegistry.getSessionsByUserId(userId2)).hasSize(1);

        // 아무 관련 없는 세션 삭제시도해도 아무 일 일어나지 않는 것 확인
        sessionRegistry.removeSession(session2);

    }


}