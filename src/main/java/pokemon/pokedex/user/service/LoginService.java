package pokemon.pokedex.user.service;

import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;

public interface LoginService {

    SessionUserDTO checkLogin(LoginDTO loginDTO);
}
