package pokemon.pokedex.user.service;

import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.RegisterResponseDTO;

public interface RegisterService {
    void validateDuplicatedLoginId(String loginId);

    RegisterResponseDTO addUser(RegisterDTO registerDTO);
}
