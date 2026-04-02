package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.FeedSchedItemDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;

/**
 * Mapper segédosztály a(z) FeedSchedItem konverziókhoz.
 */
public final class FeedSchedItemMapper {

    /**
     * Statikus segédosztály, példányosítása nem engedélyezett.
     */
    private FeedSchedItemMapper() {}

    /**
     * FeedSchedItem entitást DTO-vá alakít.
     *
     * @param feedSchedItem etetési tétel entitás
     * @return DTO
     */
    public static FeedSchedItemDTO toDTO(FeedSchedItem feedSchedItem) {
        FeedSchedItemDTO dto = new FeedSchedItemDTO();
        dto.setFeedSchedId(feedSchedItem.getFeedSched().getId());
        dto.setItemId(feedSchedItem.getItem().getId());
        dto.setFeedDescription(feedSchedItem.getFeedSched().getDescription());
        dto.setItemName(feedSchedItem.getItem().getName());
        dto.setAmount(feedSchedItem.getAmount());
        return dto;
    }
}
