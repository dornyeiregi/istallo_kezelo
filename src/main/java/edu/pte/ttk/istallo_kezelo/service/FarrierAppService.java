package edu.pte.ttk.istallo_kezelo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.dto.FarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.repository.FarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;

@Service
public class FarrierAppService {

    private final FarrierAppRepository farrierAppRepository;
    private final HorseRepository horseRepository;

    public FarrierAppService(FarrierAppRepository farrierAppRepository, HorseRepository horseRepository) {
        this.farrierAppRepository = farrierAppRepository; 
        this.horseRepository = horseRepository;
    }
    
    // Új patkolás hozzáadása
    @Transactional
    public FarrierApp createFarrierApp(FarrierAppDTO dto) {
        FarrierApp farrierApp = new FarrierApp();
        farrierApp.setAppointmentDate(dto.getAppointmentDate());
        farrierApp.setFarrierName(dto.getFarrierName());
        farrierApp.setFarrierPhone(dto.getFarrierPhone());
        farrierApp.setShoes(dto.getShoes());

        farrierApp = farrierAppRepository.save(farrierApp);

        if (dto.getHorseIds() != null) {
            for (Long horseId : dto.getHorseIds()) {
                addHorseToFarrierApp(farrierApp.getId(), horseId);
            }
        }

        return farrierApp;
    }

    // Összes patkolás lekérdezése
    public List<FarrierApp> getAllFarrierApps() {
        return farrierAppRepository.findAll();
    }

    // Patkolás lekérdezése id alaján
    public FarrierApp getFarrierAppById(Long id) {
        return farrierAppRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patkolás nem található."));
    }

    // Patkolás lekérdezése dátum alaján
    public List<FarrierApp> getFarrierAppsByDate(LocalDate date) {
        return farrierAppRepository.findByAppointmentDate(date);
    }

    // Patkolás lekérdezése patkolókovács neve alaján
    public List<FarrierApp> getFarrierAppsByFarrierName(String farrierName) {
        return farrierAppRepository.findByFarrierName(farrierName);
    }

    // Patkolás lekérdezése ló neve alaján
    public List<FarrierApp> getFarrierAppsByHorseName(String horseName) {
        return farrierAppRepository.findByHorsesDone_Horse_HorseName(horseName);
    }

    // Patkolás lekérdezése ló id alaján
    public List<FarrierApp> getFarrierAppByHorseId(Long horseId) {
        return farrierAppRepository.findAll().stream()
                .filter(app -> app.getHorses_done().stream()
                        .anyMatch(horseApp -> horseApp.getHorse().getId().equals(horseId)))
                .toList();
    }


    // Patkolás frissítése
    @Transactional
    public void updateFarrierApp(Long id, FarrierAppDTO dto) {
        FarrierApp existingFarrierApp = farrierAppRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patkolás nem található"));
        
        if(dto.getAppointmentDate() != null) {
            existingFarrierApp.setAppointmentDate(dto.getAppointmentDate());
        }
        if(dto.getFarrierName() != null) {
            existingFarrierApp.setFarrierName(dto.getFarrierName());
        }
        if(dto.getFarrierPhone() != null) {
            existingFarrierApp.setFarrierPhone(dto.getFarrierPhone());
        }
        if(dto.getShoes() != null) {
            existingFarrierApp.setShoes(dto.getShoes());
        }
        if(dto.getHorseIds() != null) {
            existingFarrierApp.getHorses_done().clear();

            for(Long horseId : dto.getHorseIds()) {
                Horse horse = horseRepository.findById(horseId)
                    .orElseThrow(() -> new RuntimeException("Ló nem található"));

                HorseFarrierApp link = new HorseFarrierApp();
                link.setHorse(horse);
                link.setFarrierApp(existingFarrierApp);
                existingFarrierApp.getHorses_done().add(link);
            }
        }
        farrierAppRepository.save(existingFarrierApp);

    }


    // Patkolás törlése
    public void deleteFarrierApp(Long id) {
        farrierAppRepository.deleteById(id);
    }

    // Ló csatolása patkoláshoz
    public void addHorseToFarrierApp(Long id, Long horseId) {
        FarrierApp farrierApp = farrierAppRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patkolás nem található"));
        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("Ló nem található"));

        HorseFarrierApp link = new HorseFarrierApp();
        link.setHorse(horse);
        link.setFarrierApp(farrierApp);
        farrierApp.getHorses_done().add(link);
        farrierAppRepository.save(farrierApp);
    }

}
