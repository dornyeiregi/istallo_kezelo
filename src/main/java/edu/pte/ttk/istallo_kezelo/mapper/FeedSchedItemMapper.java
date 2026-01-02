package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.FeedSchedItemDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;

public final class FeedSchedItemMapper {
    private FeedSchedItemMapper() {}

    public static FeedSchedItemDTO toDTO(FeedSchedItem feedSchedItem) {
        FeedSchedItemDTO dto = new FeedSchedItemDTO();
        dto.setFeedSchedId(feedSchedItem.getFeedSched().getId());
        dto.setItemId(feedSchedItem.getItem().getId());
        dto.setFeedDescription(feedSchedItem.getFeedSched().getDescription());
        dto.setItemName(feedSchedItem.getItem().getName());
        return dto;
    }
}
