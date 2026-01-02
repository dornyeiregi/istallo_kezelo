package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.enums.FeedTime;

import java.util.List;


@Repository
public interface FeedSchedRepository extends JpaRepository<FeedSched, Long> {

    List<FeedSched> findByHorseFeedScheds_Horse_Id(Long horseId);

    List<FeedSched> findByFeedTime(FeedTime feedTime);

    List<FeedSched> findByHorseFeedScheds_Horse_HorseName(String horseName);
}