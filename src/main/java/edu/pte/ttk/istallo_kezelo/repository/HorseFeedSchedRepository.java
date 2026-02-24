package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import java.util.List;

@Repository
public interface HorseFeedSchedRepository extends JpaRepository<HorseFeedSched, Long> {
    boolean existsByFeedSchedAndHorse(FeedSched feedSched, Horse horse);
    void deleteByHorse_IdAndFeedSched_Id(Long horseId, Long feedSchedId);
    List<HorseFeedSched> findByFeedSched_Id(Long feedSchedId);
    List<HorseFeedSched> findByHorse_Id(Long horseId);
    int countByFeedSchedId(Long feedSchedid);
    List<HorseFeedSched> findByHorseId(Long horseId);
}
