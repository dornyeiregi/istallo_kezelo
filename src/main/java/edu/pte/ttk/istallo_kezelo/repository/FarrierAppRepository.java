package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;

@Repository
public interface FarrierAppRepository extends JpaRepository<FarrierApp, Long> {
    List<FarrierApp> findByAppointmentDate(LocalDate date);
    List<FarrierApp> findByFarrierName(String farrierName);
    List<FarrierApp> findByHorsesDone_Horse_HorseName(String horseName);
}
