package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.Item;

/**
 * Spring Data repository a(z) Item entitásokhoz.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
