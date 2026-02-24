package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;

@Service
public class FeedSchedItemService {
    private final FeedSchedItemRepository feedSchedItemRepository;
    private final FeedSchedRepository feedSchedRepository;
    private final ItemRepository itemRepository;
    private final StorageService storageService;

    public FeedSchedItemService(FeedSchedItemRepository feedSchedItemRepository,
                                FeedSchedRepository feedSchedRepository,
                                ItemRepository itemRepository,
                                StorageService storageService) {
        this.feedSchedItemRepository = feedSchedItemRepository;
        this.feedSchedRepository = feedSchedRepository;
        this.itemRepository = itemRepository;
        this.storageService = storageService;
    }

    @Transactional
    public FeedSchedItem addItemToFeedSched(Long feedSchedId, Long itemId, Double amount){
        FeedSched feedSched = feedSchedRepository.findById(feedSchedId)
            .orElseThrow(() -> new RuntimeException("Etetési napló nem található."));
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Tétel nem található."));
        boolean exists = feedSchedItemRepository.existsByFeedSchedAndItem(feedSched, item);
        if (exists) {
            throw new RuntimeException("Tétel már hozzá van rendelve az etetési naplóhoz.");
        }
        if (amount == null) {
            throw new RuntimeException("Mennyiség kötelező.");
        }
        FeedSchedItem link = new FeedSchedItem();
        link.setFeedSched(feedSched);
        link.setItem(item);
        link.setAmount(amount);
        FeedSchedItem saved = feedSchedItemRepository.save(link);
        storageService.syncAmountInUseForItem(itemId);
        return saved;
    }

    public List<FeedSchedItem> getAllFeedSchedItems(){
        return feedSchedItemRepository.findAll();
    }

    public List<FeedSchedItem> getItemsForFeedSched(Long feedSchedId){
        return feedSchedItemRepository.findByFeedSched_Id(feedSchedId);
    }

    public List<FeedSchedItem> getFeedSchedsForItem(Long itemId){
        return feedSchedItemRepository.findByItem_Id(itemId);
    }

    public FeedSchedItem getFeedSchedItemById(Long id){
        return feedSchedItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Link nem található."));
    }

    @Transactional
    public void removeItemFromFeedSched(Long feedSchedId, Long itemId){
        feedSchedItemRepository.deleteByFeedSched_IdAndItem_Id(feedSchedId, itemId);
        storageService.syncAmountInUseForItem(itemId);
    }
}
