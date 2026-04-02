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

/**
 * Etetési ütemterv tételek kezelésére szolgáló alkalmazásszolgáltatás.
 */
@Service
public class FeedSchedItemService {
    private final FeedSchedItemRepository feedSchedItemRepository;
    private final FeedSchedRepository feedSchedRepository;
    private final ItemRepository itemRepository;
    private final StorageService storageService;

    /**
     * Létrehozza a szolgáltatást a szükséges repository-kkal.
     *
     * @param feedSchedItemRepository etetési tétel repository
     * @param feedSchedRepository     etetési ütemterv repository
     * @param itemRepository          tétel repository
     * @param storageService          tároló szolgáltatás
     */
    public FeedSchedItemService(FeedSchedItemRepository feedSchedItemRepository,
                                FeedSchedRepository feedSchedRepository,
                                ItemRepository itemRepository,
                                StorageService storageService) {
        this.feedSchedItemRepository = feedSchedItemRepository;
        this.feedSchedRepository = feedSchedRepository;
        this.itemRepository = itemRepository;
        this.storageService = storageService;
    }

    /**
     * Tétel hozzáadása etetési ütemtervhez.
     *
     * @param feedSchedId ütemterv azonosító
     * @param itemId      tétel azonosító
     * @param amount      mennyiség
     * @return mentett kapcsolat
     */
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

    /**
     * Összes etetési tétel lekérése.
     *
     * @return tételek listája
     */
    public List<FeedSchedItem> getAllFeedSchedItems(){
        return feedSchedItemRepository.findAll();
    }

    /**
     * Ütemtervhez tartozó tételek lekérése.
     *
     * @param feedSchedId ütemterv azonosító
     * @return tételek listája
     */
    public List<FeedSchedItem> getItemsForFeedSched(Long feedSchedId){
        return feedSchedItemRepository.findByFeedSched_Id(feedSchedId);
    }

    /**
     * Tételhez tartozó ütemtervi tételek lekérése.
     *
     * @param itemId tétel azonosító
     * @return tételek listája
     */
    public List<FeedSchedItem> getFeedSchedsForItem(Long itemId){
        return feedSchedItemRepository.findByItem_Id(itemId);
    }

    /**
     * Etetési tétel kapcsolat lekérése azonosító alapján.
     *
     * @param id kapcsolat azonosító
     * @return kapcsolat
     */
    public FeedSchedItem getFeedSchedItemById(Long id){
        return feedSchedItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Link nem található."));
    }

    /**
     * Tétel eltávolítása etetési ütemtervből.
     *
     * @param feedSchedId ütemterv azonosító
     * @param itemId      tétel azonosító
     */
    @Transactional
    public void removeItemFromFeedSched(Long feedSchedId, Long itemId){
        feedSchedItemRepository.deleteByFeedSched_IdAndItem_Id(feedSchedId, itemId);
        storageService.syncAmountInUseForItem(itemId);
    }
}
