package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.Stable;

/**
 * Spring Data repository a(z) Stable entitásokhoz.
 */
@Repository
public interface StableRepository  extends JpaRepository<Stable, Long> {
    Stable findByStableName(String stableName);
}
