package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.Treatment;

/**
 * Spring Data repository a(z) Treatment entitásokhoz.
 */
@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    List<Treatment> findAllByHorsesTreated_Horse_Id(Long horseId);
}
