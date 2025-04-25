package pokemon.pokedex.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.RegisterResponseDTO;
import pokemon.pokedex.user.exception.DuplicateLoginIdException;
import pokemon.pokedex.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;

    @Override
    public RegisterResponseDTO addUser(RegisterDTO registerDTO) {
        User requestUser = User.createByRegisterDto(registerDTO);
        User responseUser = userRepository.save(requestUser);
        return RegisterResponseDTO.createByUser(responseUser);
    }

    @Override
    public void validateDuplicatedLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new DuplicateLoginIdException(loginId + " already exists");
        }
    }
}
