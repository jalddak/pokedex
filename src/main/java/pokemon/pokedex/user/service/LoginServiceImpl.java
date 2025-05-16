package pokemon.pokedex.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.exception.LoginFailedException;
import pokemon.pokedex.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;

    @Override
    public SessionUserDTO checkLogin(LoginDTO loginDTO) {
        log.debug("Checking login, loginId: {}", loginDTO.getLoginId());
        return userRepository.findByLoginId(loginDTO.getLoginId())
                .filter(u -> !u.getIsDeleted())
                .filter(u -> checkPassword(loginDTO.getPassword(), u.getPassword()))
                .map(SessionUserDTO::createByUser)
                .orElseThrow(() -> new LoginFailedException("Please check your loginId or password"));
    }

    private boolean checkPassword(String rawPassword, String encodedPassword) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (rawPassword == null) throw new LoginFailedException("Please check your loginId or password");
        log.debug("Matching password");
        return encoder.matches(rawPassword, encodedPassword);
    }
}
