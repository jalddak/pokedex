package pokemon.pokedex;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.repository.UserRepository;
import pokemon.pokedex.user.service.LoginService;

import java.util.List;

@Slf4j
@Configuration
@Profile("test")
@RequiredArgsConstructor
public class TestDataInit {

    private final UserRepository userRepository;
    private final LoginService loginService;
    private final SessionRegistry sessionRegistry;

    private final List<String> adminInfos = List.of(TestDataConst.ADMIN_USERNAME,
            TestDataConst.ADMIN_LOGIN_ID, TestDataConst.ADMIN_EMAIL, TestDataConst.ADMIN_PASSWORD);
    private final List<String> user1Infos = List.of(TestDataConst.USER1_USERNAME,
            TestDataConst.USER1_LOGIN_ID, TestDataConst.USER1_EMAIL, TestDataConst.USER1_PASSWORD);
    private final List<String> user2Infos = List.of(TestDataConst.USER2_USERNAME,
            TestDataConst.USER2_LOGIN_ID, TestDataConst.USER2_EMAIL, TestDataConst.USER2_PASSWORD);
    private final List<String> testInfos = List.of(TestDataConst.TEST_USERNAME,
            TestDataConst.TEST_LOGIN_ID, TestDataConst.TEST_EMAIL, TestDataConst.TEST_PASSWORD);

    @EventListener(ApplicationReadyEvent.class)
    public void initTestData() {
        // 1. UserRepository에 admin, 일반 유저, 관리자 신청한 유저 추가
        initUserRepository();

        // 2. SessionRegistry에 세션 추가
        initSessionRegistry();
    }

    public void initSessionRegistry() {
        log.info("--- SessionRegistry 초기값 설정 시작 ---");
        SessionUserDTO testSessionUserDTO = loginService.checkLogin(initLoginDTO(testInfos));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.SESSION_USER_DTO, testSessionUserDTO);

        sessionRegistry.addSession(testSessionUserDTO.getId(), session);
        log.info("--- SessionRegistry 초기값 설정 완료 ---");
    }

    public void initUserRepository() {
        log.info("--- UserRepository 초기값 설정 시작 ---");
        User admin = initUser(adminInfos);
        admin.setRole(Role.ADMIN);

        User user1 = initUser(user1Infos);

        User user2 = initUser(user2Infos);
        user2.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        User testUser = initUser(testInfos);

        userRepository.save(admin);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(testUser);
        log.info("--- UserRepository 초기값 설정 완료 ---");
    }

    private User initUser(List<String> infos) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername(infos.get(0));
        registerDTO.setLoginId(infos.get(1));
        registerDTO.setEmail(infos.get(2));
        registerDTO.setPassword(infos.get(3));
        registerDTO.setConfirmPassword(infos.get(3));

        User user = User.createByRegisterDto(registerDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        return user;
    }

    private LoginDTO initLoginDTO(List<String> infos) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLoginId(infos.get(1));
        loginDTO.setPassword(infos.get(3));
        return loginDTO;
    }
}
