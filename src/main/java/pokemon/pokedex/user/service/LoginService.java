package pokemon.pokedex.user.service;

import pokemon.pokedex.user.dto.CheckedUserDTO;
import pokemon.pokedex.user.dto.LoginDTO;

public interface LoginService {

    CheckedUserDTO checkLogin(LoginDTO loginDTO);
}
