package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;

/**
 * Application service for linking horses to feed schedules.
 */
@Service
public class HorseFeedSchedService {
    private final FeedSchedRepository feedSchedRepository;
    private final HorseRepository horseRepository;
    private final HorseFeedSchedRepository horseFeedSchedRepository;
    private final StorageService storageService;

    public HorseFeedSchedService(FeedSchedRepository feedSchedRepository,
                                 HorseRepository horseRepository,
                                 HorseFeedSchedRepository horseFeedSchedRepository,
                                 StorageService storageService) {
        this.feedSchedRepository = feedSchedRepository;
        this.horseRepository = horseRepository;
        this.horseFeedSchedRepository = horseFeedSchedRepository;
        this.storageService = storageService;
    }

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
        HorseFeedSched saved = horseFeedSchedRepository.save(link);
        for (FeedSchedItem itemLink : feedSched.getFeedSchedItems()) {
            storageService.syncAmountInUseForItem(itemLink.getItem().getId());
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public List<HorseFeedSched> getAllHorseFeedScheds(){
        return horseFeedSchedRepository.findAll();
    }

    public HorseFeedSched getHorseFeedSchedById(Long id) {
        return horseFeedSchedRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("HorseFeedSched not found"));
    }

    public List<HorseFeedSched> getHorsesForFeedSChed(Long feedSchedId){
        return horseFeedSchedRepository.findByFeedSched_Id(feedSchedId);
    }

    public List<HorseFeedSched> getFeedSchedsForHorse(Long horseId){
        return horseFeedSchedRepository.findByHorse_Id(horseId);
    }

    @Transactional
    public void removeHorseFromFeedSched(Long feedSchedId, Long horseId){
        FeedSched feedSched = feedSchedRepository.findById(feedSchedId)
            .orElseThrow(() -> new RuntimeException("Etetési napló nem található"));
        horseFeedSchedRepository.deleteByHorse_IdAndFeedSched_Id(horseId, feedSchedId);
        for (FeedSchedItem itemLink : feedSched.getFeedSchedItems()) {
            storageService.syncAmountInUseForItem(itemLink.getItem().getId());
        }
    }

    @Transactional
    public void removeAllFeedSchedsForHorse(Long horseId) {
        List<HorseFeedSched> links = horseFeedSchedRepository.findByHorse_Id(horseId);
        if (links.isEmpty()) {
            return;
        }
        java.util.Set<Long> affectedItemIds = new java.util.HashSet<>();
        for (HorseFeedSched link : links) {
            FeedSched feedSched = link.getFeedSched();
            for (FeedSchedItem itemLink : feedSched.getFeedSchedItems()) {
                affectedItemIds.add(itemLink.getItem().getId());
            }
        }
        horseFeedSchedRepository.deleteByHorse_Id(horseId);
        for (Long itemId : affectedItemIds) {
            storageService.syncAmountInUseForItem(itemId);
        }
    }
}
