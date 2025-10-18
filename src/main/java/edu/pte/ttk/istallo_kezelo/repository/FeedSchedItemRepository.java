package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Item;


@Repository
public interface FeedSchedItemRepository extends JpaRepository<FeedSchedItem, Long> {

    void deleteByFeedSched_FeedSchedidAndItem_ItemId(Long feedSchedId, Long itemId);

    boolean existsByFeedSchedAndItem(FeedSched feedSched, Item item);

    List<FeedSchedItem> findByFeedSched_FeedSchedid(Long feedSchedId);

    List<FeedSchedItem> findByItem_ItemId(Long itemId);
}
