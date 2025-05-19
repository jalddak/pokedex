package pokemon.pokedex.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex._global.LogMessage;
import pokemon.pokedex._global.SessionConst;
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
    public void requestAdminRole(SessionUserDTO sessionUserDTO) {
        long userId = sessionUserDTO.getId();
        AdminRequestStatus adminRequestStatus = sessionUserDTO.getAdminRequestStatus();
        int updateCnt = 0;
        if (adminRequestStatus == AdminRequestStatus.NONE || adminRequestStatus == AdminRequestStatus.REJECTED)
            updateCnt = userRepository.updateAdminRequestStatusById(userId, AdminRequestStatus.REQUESTED);
        
        if (updateCnt == 0) {
            log.debug("Nothing updated in DB, userId = {}", userId);
            return;
        }

        updateSession(userId);
    }

    private void updateSession(Long userId) {
        log.debug("Updated userId: {}'s adminRequestStatus in user DB", userId);
        sessionRegistry.getSessionsByUserId(userId)
                .forEach(session -> {
                    try {
                        SessionUserDTO sessionUserDTO = (SessionUserDTO) session.getAttribute(SessionConst.SESSION_USER_DTO);
                        sessionUserDTO.setAdminRequestStatus(AdminRequestStatus.REQUESTED);
                        session.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO);
                        log.debug("Updated session for user, userId: {}, sessionId: {}", userId, session.getId());
                    } catch (Exception e) {
                        log.warn(LogMessage.SESSION_EXCEPTION_LOG, e);
                    }
                });
    }
}
