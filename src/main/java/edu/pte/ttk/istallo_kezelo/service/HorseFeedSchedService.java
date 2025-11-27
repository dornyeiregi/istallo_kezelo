package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;

@Service
public class HorseFeedSchedService {
    private final FeedSchedRepository feedSchedRepository;
    private final HorseRepository horseRepository;
    private final HorseFeedSchedRepository horseFeedSchedRepository;

    public HorseFeedSchedService(FeedSchedRepository feedSchedRepository, HorseRepository horseRepository, HorseFeedSchedRepository horseFeedSchedRepository) {
        this.feedSchedRepository = feedSchedRepository;
        this.horseRepository = horseRepository;
        this.horseFeedSchedRepository = horseFeedSchedRepository;
    }

    // Ló hozzáadása etetési naplóhoz
    @Transactional
    public HorseFeedSched addHorseToFeedSched(Long feedSchedId, Long horseId){
        FeedSched feedSched = feedSchedRepository.findById(feedSchedId)
            .orElseThrow(() -> new RuntimeException("Etetési napló nem található"));

        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("Ló nem található"));

        boolean exists = horseFeedSchedRepository.existsByFeedSchedAndHorse(feedSched, horse);
        if (exists) {
            throw new RuntimeException("A ló már hozzá van adva ehhez az etetési naplóhoz");
        }

        HorseFeedSched link = new HorseFeedSched();
        link.setFeedSched(feedSched);
        link.setHorse(horse);

        return horseFeedSchedRepository.save(link);
    }

    // Összes link lekérdezése
    @Transactional(readOnly = true)
    public List<HorseFeedSched> getAllHorseFeedScheds(){
        return horseFeedSchedRepository.findAll();
    }

    // Link lekérdezése id alapján
    public HorseFeedSched getHorseFeedSchedById(Long id) {
        return horseFeedSchedRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("HorseFeedSched not found"));
    }

    // Összes ló lekérdezése etetési naplóhoz
    public List<HorseFeedSched> getHorsesForFeedSChed(Long feedSchedId){
        return horseFeedSchedRepository.findByFeedSched_Id(feedSchedId);
    }

    // Lóhoz tartozó összes etetési napló lekérdezése
    public List<HorseFeedSched> getFeedSchedsForHorse(Long horseId){
        return horseFeedSchedRepository.findByHorse_Id(horseId);
    }

    // Ló eltávolítása etetési naplóból
    @Transactional
    public void removeHorseFromFeedSched(Long feedSchedId, Long horseId){
        horseFeedSchedRepository.deleteByHorse_IdAndFeedSched_Id(horseId, feedSchedId);
    }
}
