package pokemon.pokedex.user.service;

import pokemon.pokedex.user.dto.SessionUserDTO;

public interface UserService {

    SessionUserDTO getRealUserDTO(SessionUserDTO sessionUserDTO);

    void requestAdminRole(SessionUserDTO sessionUserDTO);
}
