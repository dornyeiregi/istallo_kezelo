package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Item;

/**
 * Spring Data repository a(z) FeedSchedItem entitásokhoz.
 */
@Repository
public interface FeedSchedItemRepository extends JpaRepository<FeedSchedItem, Long> {
    /**
     * Tétel törlése ütemterv és tétel azonosító alapján.
     *
     * @param feedSchedId ütemterv azonosító
     * @param itemId      tétel azonosító
     */
    void deleteByFeedSched_IdAndItem_Id(Long feedSchedId, Long itemId);

    /**
     * Ellenőrzi, hogy létezik-e adott ütemtervhez tartozó tétel.
     *
     * @param feedSched ütemterv
     * @param item      tétel
     * @return igaz, ha létezik
     */
    boolean existsByFeedSchedAndItem(FeedSched feedSched, Item item);

    /**
     * Ütemtervhez tartozó tételek lekérése.
     *
     * @param feedSchedId ütemterv azonosító
     * @return tételek listája
     */
    List<FeedSchedItem> findByFeedSched_Id(Long feedSchedId);

    /**
     * Tételhez tartozó ütemterv tételek lekérése.
     *
     * @param itemId tétel azonosító
     * @return tételek listája
     */
    List<FeedSchedItem> findByItem_Id(Long itemId);
}
