package edu.pte.ttk.istallo_kezelo.repository;

import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HorseRepository extends JpaRepository<Horse, Long> {
    
    Horse findByHorseName(String horseName);

    void deleteByHorseName(String horseName);

    boolean existsByHorseName(String horseName);

    List<Horse> findByOwner(User user);

}
