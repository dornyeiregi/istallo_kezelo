package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.repository.FarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;

@Service
public class HorseFarrierAppService {
    private final FarrierAppRepository farrierAppRepository;
    private final HorseRepository horseRepository;
    private final HorseFarrierAppRepository horseFarrierAppRepository;

    public HorseFarrierAppService(FarrierAppRepository farrierAppRepository, 
                                  HorseRepository horseRepository, 
                                  HorseFarrierAppRepository horseFarrierAppRepository) {
        this.farrierAppRepository = farrierAppRepository;
        this.horseRepository = horseRepository;
        this.horseFarrierAppRepository = horseFarrierAppRepository;
    }

    // Ló hozzáadása patkoláshoz
    @Transactional
    public HorseFarrierApp addHorseToFarrierApp(Long farrierAppId, Long horseId){
        FarrierApp app = farrierAppRepository.findById(farrierAppId)
            .orElseThrow(() -> new RuntimeException("Patkolás nem található."));

        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));

        boolean exists = horseFarrierAppRepository.existsByFarrierAppAndHorse(app, horse);
        if (exists) {
            throw new RuntimeException("A ló már hozzá van csatolva a patkoláshoz.");
        }

        HorseFarrierApp link = new HorseFarrierApp();
        link.setFarrierApp(app);
        link.setHorse(horse);

        return horseFarrierAppRepository.save(link);
    }

    // Összes link lekérdezése
    @Transactional(readOnly = true)
    public List<HorseFarrierApp> getAllHorseFarrierApps(){
        return horseFarrierAppRepository.findAll();
    }

    // Ló törlése patkolásból
    @Transactional
    public void removeHorseFromFarrierApp(Long farrierAppId, Long horseId){
        horseFarrierAppRepository.deleteByFarrierApp_IdAndHorse_Id(farrierAppId, horseId);
    }

    // Összes ló lekérdezése patkolás id alapján
    public List<HorseFarrierApp> getHorsesForFarrierApp(Long farrierAppId){
        return horseFarrierAppRepository.findByFarrierApp_Id(farrierAppId);
    }

    // Összes patkolás lekérdezése ló id alapján
    public List<HorseFarrierApp> getFarrierAppsForHorse(Long horseId){
        return horseFarrierAppRepository.findByHorse_Id(horseId);
    }

    // Link lekérdezése id alapján
    public HorseFarrierApp getHorseFarrierAppById(Long id){
        return horseFarrierAppRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Link nem található."));
    }
}
