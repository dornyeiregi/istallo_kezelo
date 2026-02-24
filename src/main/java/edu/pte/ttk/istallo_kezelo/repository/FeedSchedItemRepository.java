package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Item;

@Repository
public interface FeedSchedItemRepository extends JpaRepository<FeedSchedItem, Long> {
    void deleteByFeedSched_IdAndItem_Id(Long feedSchedId, Long itemId);
    boolean existsByFeedSchedAndItem(FeedSched feedSched, Item item);
    List<FeedSchedItem> findByFeedSched_Id(Long feedSchedId);
    List<FeedSchedItem> findByItem_Id(Long itemId);
}
