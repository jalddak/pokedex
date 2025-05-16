package pokemon.pokedex.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.RegisterResponseDTO;
import pokemon.pokedex.user.exception.DuplicateLoginIdException;
import pokemon.pokedex.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;

    @Override
    public RegisterResponseDTO addUser(RegisterDTO registerDTO) {
        User requestUser = User.createByRegisterDto(registerDTO);
        requestUser.setPassword(encodePassword(requestUser.getPassword()));

        User savedUser = userRepository.save(requestUser);
        return RegisterResponseDTO.createByUser(savedUser);
    }

    private String encodePassword(String rawPassword) {
        // BCryptPasswordEncoder(값) 기본값:10 (보안 강도)
        log.debug("Encoding password");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(rawPassword);
    }

    @Override
    public void validateDuplicatedLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new DuplicateLoginIdException(loginId + " already exists");
        }
    }
}
