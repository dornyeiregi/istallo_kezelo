package edu.pte.ttk.istallo_kezelo.repository;

import edu.pte.ttk.istallo_kezelo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUserLnameAndUserFname(String userLname, String userFame);
    
    public User findByUsername(String username);

    public User findByOwnedHorsesHorseName(String horseName);

    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);
}
