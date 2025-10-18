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
        farrierApp.setAppointmentDate(dto.appointmentDate);
        farrierApp.setFarrierName(dto.farrierName);
        farrierApp.setFarrierPhone(dto.farrierPhone);
        farrierApp.setShoes(dto.shoes);

        farrierApp = farrierAppRepository.save(farrierApp);

        if (dto.horseIds != null) {
            for (Long horseId : dto.horseIds) {
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
    public Iterable<FarrierApp> getFarrierAppByHorseId(Long horseId) {
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
        
        if(dto.appointmentDate != null) {
            existingFarrierApp.setAppointmentDate(dto.appointmentDate);
        }
        if(dto.farrierName != null) {
            existingFarrierApp.setFarrierName(dto.farrierName);
        }
        if(dto.farrierPhone != null) {
            existingFarrierApp.setFarrierPhone(dto.farrierPhone);
        }
        if(dto.shoes != null) {
            existingFarrierApp.setShoes(dto.shoes);
        }
        if(dto.horseIds != null) {
            existingFarrierApp.getHorses_done().clear();

            for(Long horseId : dto.horseIds) {
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
