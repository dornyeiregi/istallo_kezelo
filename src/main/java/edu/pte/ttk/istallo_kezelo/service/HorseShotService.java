package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.ShotRepository;

@Service
public class HorseShotService {
    private final HorseRepository horseRepository;
    private final ShotRepository shotRepository;
    private final HorseShotRepository horseShotRepository;

    public HorseShotService (HorseRepository horseRepository, ShotRepository shotRepository, HorseShotRepository horseShotRepository){
        this.horseRepository = horseRepository;
        this.shotRepository = shotRepository;
        this.horseShotRepository = horseShotRepository;
    }

    // Oltás hozzáadása lóhoz
    @Transactional
    public HorseShot addShotToHorse(Long shotId, Long horseId){
        Shot shot = shotRepository.findById(shotId)
            .orElseThrow(() -> new RuntimeException("Oltás nem található."));

        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));

        boolean exists = horseShotRepository.existsByShotAndHorse(shot, horse);
        if (exists) {
            throw new RuntimeException("Az oltás már hozzá van csatolva a lóhoz.");
        }

        HorseShot link = new HorseShot();
        link.setShot(shot);
        link.setHorse(horse);

        return horseShotRepository.save(link);
    }

    // Összes link lekérdezése
    @Transactional(readOnly = true)
    public List<HorseShot> getAllHorseShots(){
        return horseShotRepository.findAll();
    }

    // Link lekérdezése id alapján
    public HorseShot getHorseShotById(Long horseShotId){
        return horseShotRepository.findById(horseShotId)
            .orElseThrow(() -> new RuntimeException("Link nem található."));
    }

    // Oltáshoz tartozó összes ló lekérdezése
    public List<HorseShot> getHorseForShot(Long shotId){
        return horseShotRepository.findByShot_ShotId(shotId);
    }

    // Lóhoz tartozó összes oltás lekérdezése
    public List<HorseShot> getShotsForHorse(Long horseId){
        return horseShotRepository.findByHorse_Id(horseId);
    }

    // Oltás eltávolítása lóból
    @Transactional
    public void removeShotFromHorse(Long shotId, Long horseId){
        horseShotRepository.deleteByShot_ShotIdAndHorse_Id(shotId, horseId);
    }
}
