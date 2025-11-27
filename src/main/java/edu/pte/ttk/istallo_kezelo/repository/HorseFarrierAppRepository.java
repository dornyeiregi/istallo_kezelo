package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;

@Repository
public interface HorseFarrierAppRepository extends JpaRepository<HorseFarrierApp, Long> {

    void deleteByFarrierApp_IdAndHorse_Id(Long farrierAppId, Long horseId);

    List<HorseFarrierApp> findByFarrierApp_Id(Long farrierAppId);

    List<HorseFarrierApp> findByHorseId(Long horseId);

    boolean existsByFarrierAppAndHorse(FarrierApp app, Horse horse);

    int countByFarrierApp_Id(Long farrierAppId);

}
