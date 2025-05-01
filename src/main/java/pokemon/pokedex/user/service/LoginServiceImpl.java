package pokemon.pokedex.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pokemon.pokedex.user.dto.CheckedUserDTO;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.exception.LoginFailedException;
import pokemon.pokedex.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;

    @Override
    public CheckedUserDTO checkLogin(LoginDTO loginDTO) {
        return userRepository.findByLoginId(loginDTO.getLoginId())
                .filter(u -> checkPassword(loginDTO.getPassword(), u.getPassword()))
                .map(CheckedUserDTO::createByUser)
                .orElseThrow(() -> new LoginFailedException("Please check your loginId or password"));
    }

    private boolean checkPassword(String rawPassword, String encodedPassword) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, encodedPassword);
    }
}
