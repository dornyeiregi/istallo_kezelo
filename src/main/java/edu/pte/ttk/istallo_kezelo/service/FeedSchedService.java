package edu.pte.ttk.istallo_kezelo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import edu.pte.ttk.istallo_kezelo.dto.FeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;


@Service
public class FeedSchedService {

    private final FeedSchedRepository feedSchedRepository;
    private final HorseRepository horseRepository;
    private final ItemRepository itemRepository;

    public FeedSchedService(FeedSchedRepository feedSchedRepository, HorseRepository horseRepository, ItemRepository itemRepository) {
        this.feedSchedRepository = feedSchedRepository;
        this.horseRepository = horseRepository;
        this.itemRepository = itemRepository;
    }

    // Új etetési napló hozzáadása
    @Transactional
    public FeedSched createFeedSched(FeedSchedDTO dto) {
        FeedSched feedSched = new FeedSched();
        feedSched.setFeedTime(dto.feedTime);
        feedSched.setDescription(dto.description);
        feedSched = feedSchedRepository.save(feedSched);

        if (dto.horseIds != null) {
            for (Long horseId : dto.horseIds) {
                addHorseToFeedSched(feedSched.getFeedSchedid(), horseId);
            }
        }

        if (dto.itemIds != null) {
            for (Long itemId : dto.itemIds) {
                addItemToFeedSched(feedSched.getFeedSchedid(), itemId);
            }
        }

        return feedSched;
    }


    // Összes etetési napló lekérdezése
    public List<FeedSched> getAllFeedScheds(){
        return feedSchedRepository.findAll();
    }

    // Etetési napló lekérdezése id alapján
    public FeedSched getFeedSchedById(Long id){
        return feedSchedRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));
    }

    // Etetési naplók lekérdezése ló id alapján
    public List<FeedSched> getFeedSchedByHorseId(Long id){
        return feedSchedRepository.findByHorseFeedScheds_Horse_Id(id);
    }

    // Etetési naplók lekérdezése ló neve alapján
    public List<FeedSched> getFeedSchedByHorseName(String horseName){
        return feedSchedRepository.findByHorseFeedScheds_Horse_HorseName(horseName);   
    }

    // Etetési napló frissítése
    @Transactional
    public void updateFeedSched(Long id, FeedSchedDTO dto){
        FeedSched existingFeedSched = feedSchedRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Etetési napló nem található."));

        if (dto.feedTime != null) {
            existingFeedSched.setFeedTime(dto.feedTime);
        }

        if (dto.description != null) {
            existingFeedSched.setDescription(dto.description);
        }

        if (dto.horseIds != null) {
            existingFeedSched.getHorseFeedScheds().clear();

            for(Long horseId : dto.horseIds){
                Horse horse = horseRepository.findById(horseId)
                    .orElseThrow(() -> new RuntimeException("Ló nem található."));

                HorseFeedSched link = new HorseFeedSched();
                link.setHorse(horse);
                link.setFeedSched(existingFeedSched);

                existingFeedSched.getHorseFeedScheds().add(link);
            }
        }

        if (dto.itemIds != null) {
            existingFeedSched.getFeedSchedItems().clear();

            for (Long itemId : dto.itemIds) {
                Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Takarmány nem található."));

                FeedSchedItem link = new FeedSchedItem();
                link.setItem(item);
                link.setFeedSched(existingFeedSched);

                existingFeedSched.getFeedSchedItems().add(link);
            }
        }
        feedSchedRepository.save(existingFeedSched);
    }


    // Etetési napló törlése
    @Transactional
    public void deleteFeedSched(Long id){
        feedSchedRepository.deleteById(id);
    }

    // Ló hozzáadása etetési naplóhoz
    @Transactional
    public void addHorseToFeedSched(Long feedSchedId, Long horseId) {
        FeedSched feedSched = feedSchedRepository.findById(feedSchedId)
            .orElseThrow(() -> new RuntimeException("Etetési napló nem található."));
        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));

        HorseFeedSched link = new HorseFeedSched();
        link.setHorse(horse);
        link.setFeedSched(feedSched);

        feedSched.getHorseFeedScheds().add(link);
        feedSchedRepository.save(feedSched);
    }

    // Tétel hozzáadása etetési naplóhoz
    @Transactional
    public void addItemToFeedSched(Long feedSchedId, Long itemId) {
        FeedSched feedSched = feedSchedRepository.findById(feedSchedId)
            .orElseThrow(() -> new RuntimeException("Etetési napló nem található."));
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Takarmány nem található."));

        FeedSchedItem link = new FeedSchedItem();
        link.setItem(item);
        link.setFeedSched(feedSched);

        feedSched.getFeedSchedItems().add(link);
        feedSchedRepository.save(feedSched);
    }

    
    
}
