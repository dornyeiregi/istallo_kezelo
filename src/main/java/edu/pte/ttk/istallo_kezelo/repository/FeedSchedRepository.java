package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import java.util.List;


/**
 * Spring Data repository a(z) FeedSched entitásokhoz.
 */
@Repository
public interface FeedSchedRepository extends JpaRepository<FeedSched, Long> {
    /**
     * Ütemtervek lekérése ló azonosító alapján.
     *
     * @param horseId ló azonosító
     * @return ütemtervek listája
     */
    List<FeedSched> findByHorseFeedScheds_Horse_Id(Long horseId);

    /**
     * Ütemtervek lekérése ló név alapján.
     *
     * @param horseName ló neve
     * @return ütemtervek listája
     */
    List<FeedSched> findByHorseFeedScheds_Horse_HorseName(String horseName);
}
