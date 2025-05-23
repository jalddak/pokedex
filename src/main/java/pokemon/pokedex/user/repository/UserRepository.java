package pokemon.pokedex.user.repository;


import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);

    int updateAdminRequestStatusById(Long id, AdminRequestStatus status);

    void deleteById(Long id);
}
