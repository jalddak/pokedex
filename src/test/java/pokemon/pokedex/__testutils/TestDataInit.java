package pokemon.pokedex.__testutils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.mock.web.MockHttpSession;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.repository.UserRepository;
import pokemon.pokedex.user.service.LoginService;

import static pokemon.pokedex.__testutils.TestDataFactory.*;

@Slf4j
@Configuration
@Profile("test")
@RequiredArgsConstructor
public class TestDataInit {
    private final UserRepository userRepository;
    private final LoginService loginService;
    private final SessionRegistry sessionRegistry;

    @EventListener(ApplicationReadyEvent.class)
    public void initTestData() {
        // 1. UserRepository에 admin, 일반 유저, 관리자 신청한 유저 추가
        initUserRepository();

        // 2. SessionRegistry에 세션 추가
        initSessionRegistry();
    }

    public void initUserRepository() {
        log.info("[initUserRepository] --- UserRepository 초기값 설정 시작 ---");

        User defaultUser = createUser(defaultInfos);

        User admin = createUser(adminInfos);
        admin.setRole(Role.ADMIN);

        User adminRequestUser = createUser(adminRequestInfos);
        adminRequestUser.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        User alreadySessionUser = createUser(alreadySessionInfos);

        User deletedUser = createUser(deletedInfos);

        userRepository.save(defaultUser);
        userRepository.save(admin);
        userRepository.save(adminRequestUser);
        userRepository.save(alreadySessionUser);
        userRepository.save(deletedUser);
        userRepository.deleteById(deletedUser.getId());
        
        log.info("[initUserRepository] --- UserRepository 초기값 설정 완료 ---");
    }

    public void initSessionRegistry() {
        log.info("[initSessionRegistry] --- SessionRegistry 초기값 설정 시작 ---");
        SessionUserDTO SessionUserDTO = loginService.checkLogin(createLoginDTO(alreadySessionInfos));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.SESSION_USER_DTO, SessionUserDTO);

        sessionRegistry.addSession(SessionUserDTO.getId(), session);
        log.info("[initSessionRegistry] --- SessionRegistry 초기값 설정 완료 ---");
    }
}
