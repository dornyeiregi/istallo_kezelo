package edu.pte.ttk.istallo_kezelo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedChangeRequest;

/**
 * Spring Data repository a(z) FeedSchedChangeRequest entitásokhoz.
 */
@Repository
public interface FeedSchedChangeRequestRepository extends JpaRepository<FeedSchedChangeRequest, Long> {
    /**
     * Minden változtatási kérelem lekérése időrendben (csökkenő).
     *
     * @return kérelmek listája
     */
    List<FeedSchedChangeRequest> findAllByOrderByRequestedAtDesc();

    /**
     * Változtatási kérelmek lekérése kérő felhasználó alapján.
     *
     * @param requestedById kérő felhasználó azonosító
     * @return kérelmek listája
     */
    List<FeedSchedChangeRequest> findAllByRequestedBy_IdOrderByRequestedAtDesc(Long requestedById);
}
