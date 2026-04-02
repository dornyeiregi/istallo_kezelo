package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;

/**
 * Spring Data repository a(z) FarrierApp entitásokhoz.
 */
@Repository
public interface FarrierAppRepository extends JpaRepository<FarrierApp, Long> {
    /**
     * Patkolások lekérése dátum alapján.
     *
     * @param date időpont dátuma
     * @return patkolások listája
     */
    List<FarrierApp> findByAppointmentDate(LocalDate date);

    /**
     * Patkolások lekérése patkoló neve alapján.
     *
     * @param farrierName patkoló neve
     * @return patkolások listája
     */
    List<FarrierApp> findByFarrierName(String farrierName);

    /**
     * Patkolások lekérése ló neve alapján.
     *
     * @param horseName ló neve
     * @return patkolások listája
     */
    List<FarrierApp> findByHorsesDone_Horse_HorseName(String horseName);
}
