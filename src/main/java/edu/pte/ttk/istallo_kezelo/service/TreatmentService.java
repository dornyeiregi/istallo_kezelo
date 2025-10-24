package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.dto.TreatmentDTO;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.repository.HorseTreatmentRepository;
import edu.pte.ttk.istallo_kezelo.repository.TreatmentRepository;

@Service
public class TreatmentService {
    private final TreatmentRepository treatmentRepository;
    private final HorseTreatmentRepository horseTreatmentRepository;

    public TreatmentService(TreatmentRepository treatmentRepository, HorseTreatmentRepository horseTreatmentRepository){
        this.treatmentRepository = treatmentRepository;
        this.horseTreatmentRepository = horseTreatmentRepository;
    }

    // Új kezelés hozzáadása
    @Transactional
    public Treatment saveTreatment(Treatment treatment){
        return treatmentRepository.save(treatment);
    }

    // Összes kezelés lekérdezése
    public List<Treatment> getAllTreatments(){
        return treatmentRepository.findAll();
    }

    // Kezelés lekérdezése id alapján
    public Treatment getTreatmentById(Long id){
        return treatmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Kezelés nem található."));
    }

    // Egy ló minden kezelésének lekérdezése ló id alapján
    @Transactional(readOnly = true)
    public List<Treatment> getTreatmentsByHorseId(Long horseId){
        List<HorseTreatment> treatments = horseTreatmentRepository.findByHorse_Id(horseId);
        return treatments.stream().map(HorseTreatment::getTreatment).toList();
    }

    // Egy ló minden kezelésének lekérdezése ló neve alapján
    @Transactional(readOnly = true)
    public List<Treatment> getTreatmentsByHorseName(String horseName){
        List<HorseTreatment> treatments = horseTreatmentRepository.findByHorse_horseName(horseName);
        return treatments.stream().map(HorseTreatment::getTreatment).toList();
    }

    // Kezelés frissítése
    @Transactional
    public Treatment updateTreatment(Long treatmentId, TreatmentDTO updatedTreatment) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new RuntimeException("Kezelés nem található."));

        if (updatedTreatment.getTreatmentName() != null) {
            treatment.setTreatmentName(updatedTreatment.getTreatmentName());
        }
        if (updatedTreatment.getDescription() != null) {
            treatment.setDescription(updatedTreatment.getDescription());
        }
        if (updatedTreatment.getDate() != null) {
            treatment.setDate(updatedTreatment.getDate());
        }

        return treatmentRepository.save(treatment);
    }


    // Delete treatment
    @Transactional
    public void deleteTreatmentById(Long id){
        treatmentRepository.deleteById(id);
    }

}
