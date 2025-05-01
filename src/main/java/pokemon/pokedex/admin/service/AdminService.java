package pokemon.pokedex.admin.service;

import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.dto.CheckedUserDTO;

public interface AdminService {

    void changeAdminRequestStatus(CheckedUserDTO checkedUserDTO, AdminRequestStatus newStatus);
}
