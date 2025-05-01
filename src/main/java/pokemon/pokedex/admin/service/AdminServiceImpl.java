package pokemon.pokedex.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.dto.CheckedUserDTO;
import pokemon.pokedex.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Override
    public void changeAdminRequestStatus(CheckedUserDTO checkedUserDTO, AdminRequestStatus newStatus) {
        if (checkedUserDTO.getAdminRequestStatus().equals(newStatus)
                || userRepository.updateAdminRequestStatusById(checkedUserDTO.getId(), newStatus) == 0) {
            throw new IllegalArgumentException("Bad Request");
        }
    }
}
