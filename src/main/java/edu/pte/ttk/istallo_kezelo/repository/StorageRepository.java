package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemCategory;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {
    Storage findByItem_Id(Long itemId);
    List<Storage> findByItem_ItemCategory(ItemCategory itemCategory);
    List<Storage> findByItem_ItemType(ItemType itemType);
}
