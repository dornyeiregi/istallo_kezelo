package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.Stable;

@Repository
public interface StableRepository  extends JpaRepository<Stable, Long> {
    public Stable findByStableName(String stableName);
}
