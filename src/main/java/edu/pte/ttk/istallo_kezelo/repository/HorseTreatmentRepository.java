package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Treatment;

/**
 * Spring Data repository a(z) HorseTreatment entitásokhoz.
 */
@Repository
public interface HorseTreatmentRepository extends JpaRepository<HorseTreatment, Long>{
    List<HorseTreatment> findByHorse_Id(Long horseId);
    boolean existsByTreatmentAndHorse(Treatment treatment, Horse horse);
    void deleteByTreatment_IdAndHorse_Id(Long treatmentId, Long horseId);
    List<HorseTreatment> findByHorse_horseName(String horseName);
    List<HorseTreatment> findByTreatment_Id(Long treatmentId);
    int countByTreatment_Id(Long treatmentId);
}
