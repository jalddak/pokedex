package pokemon.pokedex.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex._global.session.registry.SessionRegistry;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;

    @Override
    public SessionUserDTO getRealUserDTO(SessionUserDTO sessionUserDTO) {
        return userRepository.findById(sessionUserDTO.getId())
                .filter(u -> !u.getIsDeleted())
                .map(SessionUserDTO::createByUser)
                .orElse(null);
    }

    @Override
    public void requestAdminRole(Long userId) {
        int updateCnt = userRepository.updateAdminRequestStatusById(userId, AdminRequestStatus.REQUESTED);

        if (updateCnt == 0) return;

        updateSession(userId);
    }

    private void updateSession(Long userId) {
        sessionRegistry.getSessionsByUserId(userId)
                .forEach(session -> {
                    try {
                        SessionUserDTO sessionUserDTO = (SessionUserDTO) session.getAttribute(SessionConst.SESSION_USER_DTO);
                        sessionUserDTO.setAdminRequestStatus(AdminRequestStatus.REQUESTED);
                        session.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO);
                    } catch (Exception e) {
                        log.warn("세션 업데이트 중 예외 발생: {}", userId, e);
                    }
                });
    }
}
