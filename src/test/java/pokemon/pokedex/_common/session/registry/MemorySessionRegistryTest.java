package pokemon.pokedex._common.session.registry;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import pokemon.pokedex._common.session.registry.MemorySessionRegistry;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MemorySessionRegistryTest {

    @Mock
    private HttpSession session;

    private MemorySessionRegistry sessionRegistry;

    @BeforeEach
    void setUp() {
        sessionRegistry = new MemorySessionRegistry();
        sessionRegistry.clear();
    }

    @AfterEach
    void tearDown() {
        sessionRegistry.clear();
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
        // 첫 번째 세션 생성
        MockHttpSession session1 = new MockHttpSession();
        SessionUserDTO user1 = new SessionUserDTO();
        user1.setId(1L);
        user1.setUsername("user1");
        session1.setAttribute(SessionConst.SESSION_USER_DTO, user1);

        // 두 번째 세션 생성
        MockHttpSession session2 = new MockHttpSession();
        SessionUserDTO user2 = new SessionUserDTO();
        user2.setId(1L);  // 같은 userId로 두 번째 세션을 추가
        user2.setUsername("user1");
        session2.setAttribute(SessionConst.SESSION_USER_DTO, user2);

        // 세 번째 세션 생성
        MockHttpSession session3 = new MockHttpSession();
        SessionUserDTO user3 = new SessionUserDTO();
        user3.setId(2L);  // 다른 userId로 세 번째 세션을 추가
        user3.setUsername("user2");
        session3.setAttribute(SessionConst.SESSION_USER_DTO, user3);

        // 세션들을 MemorySessionRegistry에 추가
        sessionRegistry.addSession(1L, session1);
        sessionRegistry.addSession(1L, session2);
        sessionRegistry.addSession(2L, session3);

        // userId = 1인 세션 개수 확인
        List<HttpSession> sessionsForUser1 = sessionRegistry.getSessionsByUserId(1L);
        assertThat(sessionsForUser1).hasSize(2);

        // userId = 2인 세션 개수 확인
        List<HttpSession> sessionsForUser2 = sessionRegistry.getSessionsByUserId(2L);
        assertThat(sessionsForUser2).hasSize(1);
    }

    @Test
    void removeSession() {
        // 첫 번째 세션 생성
        MockHttpSession session1 = new MockHttpSession();
        SessionUserDTO user1 = new SessionUserDTO();
        user1.setId(1L);
        user1.setUsername("user1");
        session1.setAttribute(SessionConst.SESSION_USER_DTO, user1);

        // 두 번째 세션 생성
        MockHttpSession session2 = new MockHttpSession();
        SessionUserDTO user2 = new SessionUserDTO();
        user2.setId(1L);  // 같은 userId로 두 번째 세션을 추가
        user2.setUsername("user1");
        session2.setAttribute(SessionConst.SESSION_USER_DTO, user2);

        // 세 번째 세션 생성
        MockHttpSession session3 = new MockHttpSession();
        SessionUserDTO user3 = new SessionUserDTO();
        user3.setId(2L);  // 다른 userId로 세 번째 세션을 추가
        user3.setUsername("user2");
        session3.setAttribute(SessionConst.SESSION_USER_DTO, user3);

        // 세션들을 MemorySessionRegistry에 추가
        sessionRegistry.addSession(1L, session1);
        sessionRegistry.addSession(1L, session2);
        sessionRegistry.addSession(2L, session3);

        sessionRegistry.removeSession(session1);
        assertThat(sessionRegistry.getSessionsByUserId(1L)).hasSize(1);
        sessionRegistry.removeSession(session2);
        assertThat(sessionRegistry.getSessionsByUserId(1L)).isEmpty();
        assertThat(sessionRegistry.getSessionsByUserId(2L)).hasSize(1);

        sessionRegistry.removeSession(session2);

    }


}