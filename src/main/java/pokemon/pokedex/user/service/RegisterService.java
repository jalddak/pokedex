package pokemon.pokedex.user.service;

import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.RegisterResponseDTO;

public interface RegisterService {
    RegisterResponseDTO addUser(RegisterDTO registerDTO);

    void validateDuplicatedLoginId(String loginId);
}
