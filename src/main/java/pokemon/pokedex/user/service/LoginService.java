package pokemon.pokedex.user.service;

import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.LoginResponseDTO;

public interface LoginService {

    LoginResponseDTO checkLogin(LoginDTO loginDTO);
}
