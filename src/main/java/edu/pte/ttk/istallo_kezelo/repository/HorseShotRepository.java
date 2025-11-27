package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.Shot;

@Repository
public interface HorseShotRepository extends JpaRepository<HorseShot, Long> {

    List<HorseShot> findByHorse_Id(Long horseId);

    boolean existsByShotAndHorse(Shot shot, Horse horse);

    void deleteByShot_IdAndHorse_Id(Long shotId, Long horseId);

    List<HorseShot> findByShot_Id(Long shotId);

    List<HorseShot> findByHorse_horseName(String horseName);

    int countByShot_Id(Long shotId);
}
