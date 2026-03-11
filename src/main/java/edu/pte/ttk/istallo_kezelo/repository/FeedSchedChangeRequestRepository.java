package edu.pte.ttk.istallo_kezelo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedChangeRequest;

@Repository
public interface FeedSchedChangeRequestRepository extends JpaRepository<FeedSchedChangeRequest, Long> {
    List<FeedSchedChangeRequest> findAllByOrderByRequestedAtDesc();
}
