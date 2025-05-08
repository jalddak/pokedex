package pokemon.pokedex.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.exception.LoginFailedException;
import pokemon.pokedex.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;

    @Override
    public SessionUserDTO checkLogin(LoginDTO loginDTO) {
        return userRepository.findByLoginId(loginDTO.getLoginId())
                .filter(u -> !u.getIsDeleted())
                .filter(u -> checkPassword(loginDTO.getPassword(), u.getPassword()))
                .map(SessionUserDTO::createByUser)
                .orElseThrow(() -> new LoginFailedException("Please check your loginId or password"));
    }

    private boolean checkPassword(String rawPassword, String encodedPassword) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (rawPassword == null) throw new LoginFailedException("Please check your loginId or password");
        return encoder.matches(rawPassword, encodedPassword);
    }
}
