package pokemon.pokedex._test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.repository.UserRepository;

/**
 * 메모리 기반 레포지토리로 실행 시 초기값을 넣어주기 위함.
 */
@Slf4j
@Component
@Profile("memory")
@RequiredArgsConstructor
public class TempDataInit {

    private final UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        RegisterDTO adminDto = new RegisterDTO();
        adminDto.setUsername("관리자");
        adminDto.setLoginId("admin");
        adminDto.setEmail("admin@email.com");
        adminDto.setPassword("test");
        adminDto.setConfirmPassword("test");

        User admin = User.createByRegisterDto(adminDto);
        admin.setPassword(encoder.encode(admin.getPassword()));
        admin.setRole(Role.ADMIN);

        RegisterDTO userDto1 = new RegisterDTO();
        userDto1.setUsername("유저1");
        userDto1.setLoginId("user1");
        userDto1.setEmail("user1@email.com");
        userDto1.setPassword("test");
        userDto1.setConfirmPassword("test");

        User user1 = User.createByRegisterDto(userDto1);
        user1.setPassword(encoder.encode(user1.getPassword()));

        RegisterDTO userDto2 = new RegisterDTO();
        userDto2.setUsername("유저2");
        userDto2.setLoginId("user2");
        userDto2.setEmail("user2@email.com");
        userDto2.setPassword("test");
        userDto2.setConfirmPassword("test");

        User user2 = User.createByRegisterDto(userDto2);
        user2.setPassword(encoder.encode(user2.getPassword()));
        user2.setAdminRequestStatus(AdminRequestStatus.REQUESTED);

        log.debug("----- 초기값 세팅 시작 -----");
        userRepository.save(admin);
        userRepository.save(user1);
        userRepository.save(user2);
        log.debug("----- 초기값 세팅 완료 -----");
    }

}
