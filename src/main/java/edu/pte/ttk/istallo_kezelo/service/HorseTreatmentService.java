package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseTreatmentRepository;
import edu.pte.ttk.istallo_kezelo.repository.TreatmentRepository;

@Service
public class HorseTreatmentService {
    private final HorseTreatmentRepository horseTreatmentRepository;
    private final HorseRepository horseRepository;
    private final TreatmentRepository treatmentRepository;

    public HorseTreatmentService(HorseTreatmentRepository horseTreatmentRepository, HorseRepository horseRepository, TreatmentRepository treatmentRepository){
        this.horseTreatmentRepository = horseTreatmentRepository;
        this.horseRepository = horseRepository;
        this.treatmentRepository = treatmentRepository;
    }

    // Kezelés hozzáadása lóhoz
    @Transactional
    public HorseTreatment addTreatmentToHorse(Long treatmentId, Long horseId){
        Treatment treatment = treatmentRepository.findById(treatmentId)
            .orElseThrow(() -> new RuntimeException("Kezelés nem található."));

        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));
        
        boolean exists = horseTreatmentRepository.existsByTreatmentAndHorse(treatment, horse);
        if (exists) {
            throw new RuntimeException("A kezelés már hozzá van adva a lóhoz.");
        }

        HorseTreatment link = new HorseTreatment();
        link.setHorse(horse);
        link.setTreatment(treatment);

        return horseTreatmentRepository.save(link);
    }

    // Összes link lekérdezése
    @Transactional(readOnly = true)
    public List<HorseTreatment> getAllHorseTreatments(){
        return horseTreatmentRepository.findAll();
    }

    // Link lekérdezése id alapján
    public HorseTreatment getHorseTreatmentById(Long id){
        return horseTreatmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Link nem található."));
    }

    // Egy ló minden kezelésének lekérdezése ló id alapján
    public List<HorseTreatment> getTreatmentsForHorse(Long horseId){
        return horseTreatmentRepository.findByHorse_Id(horseId);
    }

    // Kezelés eltávolítása lótól
    @Transactional
    public void removeTreatmentFromHorse(Long treatmentId, Long horseId){
        horseTreatmentRepository.deleteByTreatment_TreatmentIdAndHorse_Id(treatmentId, horseId);
    }

}
