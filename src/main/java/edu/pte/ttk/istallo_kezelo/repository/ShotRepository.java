package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.Shot;

/**
 * Spring Data repository a(z) Shot entitásokhoz.
 */
@Repository
public interface ShotRepository extends JpaRepository<Shot, Long> {
}
