package edu.pte.ttk.istallo_kezelo.repository;

import edu.pte.ttk.istallo_kezelo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserLnameAndUserFname(String userLname, String userFame);
    User findByUsername(String username);
    User findByOwnedHorsesHorseName(String horseName);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
