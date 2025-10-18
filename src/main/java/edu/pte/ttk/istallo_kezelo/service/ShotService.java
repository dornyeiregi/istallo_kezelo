package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.dto.ShotDTO;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.repository.HorseShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.ShotRepository;

@Service
public class ShotService {
    private final ShotRepository shotRepository;
    private final HorseShotRepository horseShotRepository;

    public ShotService(ShotRepository shotRepository, HorseShotRepository horseShotRepository) {
        this.shotRepository = shotRepository;
        this.horseShotRepository = horseShotRepository;
    }

    // Új oltás létrehozása
    @Transactional
    public Shot saveShot(Shot shot){
        return shotRepository.save(shot);
    }

    // Összes oltás lekérdezése
    public List<Shot> getAllShots(){
        return shotRepository.findAll();
    }

    // Oltás lekérdezése id alapján
    public Shot getShotById(Long shotId){
        return shotRepository.findById(shotId)
            .orElseThrow(() -> new RuntimeException("Oltás nem található."));
    }

    // Ló összes oltásának lekérdezése ló id alapján
    @Transactional(readOnly = true)
    public List<Shot> getShotsByHorseId(Long horseId){
        List<HorseShot> horseShots = horseShotRepository.findByHorse_Id(horseId);
        return horseShots.stream().map(HorseShot::getShot).toList();
    }

    // Ló összes oltásának lekérdezése ló neve alapján
    @Transactional(readOnly = true)
    public List<Shot> getShotsByHorseName(String horseName){
        List<HorseShot> horseShots = horseShotRepository.findByHorse_horseName(horseName);
        return horseShots.stream().map(HorseShot::getShot).toList();
    }
    
    // Oltás frissítése
    @Transactional
    public Shot updateShot(Long shotId, ShotDTO updatedShot) {
        Shot shot = shotRepository.findById(shotId)
                .orElseThrow(() -> new RuntimeException("Oltás nem található."));

        if (updatedShot.shotName != null) {
            shot.setShotName(updatedShot.shotName);
        }
        if (updatedShot.date != null) {
            shot.setDate(updatedShot.date);
        }
        if (updatedShot.frequencyUnit != null) {
            shot.setFrequencyUnit(updatedShot.frequencyUnit);
        }
        if (updatedShot.frequencyValue != null) {
            shot.setFrequencyValue(updatedShot.frequencyValue);
        }

        return shotRepository.save(shot);
    }


    // Oltás törlése
    @Transactional
    public void deleteShotById(Long shotId){
        shotRepository.deleteById(shotId);
    }

}
