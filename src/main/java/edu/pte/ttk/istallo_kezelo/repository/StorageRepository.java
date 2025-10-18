package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.Storage;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    Storage findByItem_ItemId(Long itemId);

}
