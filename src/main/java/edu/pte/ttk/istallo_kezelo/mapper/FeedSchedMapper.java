package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.FeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedItemAmountDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;

public final class FeedSchedMapper {

    private FeedSchedMapper() {}

    public static FeedSchedDTO toDTO(FeedSched feedSched) {
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setFeedSchedId(feedSched.getId());
        dto.setFeedMorning(feedSched.isFeedMorning());
        dto.setFeedNoon(feedSched.isFeedNoon());
        dto.setFeedEvening(feedSched.isFeedEvening());
        dto.setDescription(feedSched.getDescription());
        dto.setHorseIds(feedSched.getHorseFeedScheds().stream()
            .map(hfs -> hfs.getHorse().getId()).toList());
        dto.setItemIds(feedSched.getFeedSchedItems().stream()
            .map(fsi -> fsi.getItem().getId()).toList());
        dto.setItems(feedSched.getFeedSchedItems().stream()
            .map(fsi -> new FeedSchedItemAmountDTO(fsi.getItem().getId(), fsi.getAmount()))
            .toList());
        return dto;
    }
}
