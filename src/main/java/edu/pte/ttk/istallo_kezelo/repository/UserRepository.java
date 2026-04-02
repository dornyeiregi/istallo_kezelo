package edu.pte.ttk.istallo_kezelo.repository;

import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository a(z) User entitásokhoz.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserLnameAndUserFname(String userLname, String userFame);
    User findByUsername(String username);
    User findByOwnedHorsesHorseName(String horseName);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByUserType(UserType userType);
    User findByEmail(String email);
}
