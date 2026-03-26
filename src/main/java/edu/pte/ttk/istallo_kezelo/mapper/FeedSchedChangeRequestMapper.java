package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.FeedSchedChangeRequestDTO;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedItemAmountDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedChangeRequest;
import java.util.List;

public final class FeedSchedChangeRequestMapper {

    private FeedSchedChangeRequestMapper() {}

    public static FeedSchedChangeRequestDTO toDTO(
            FeedSchedChangeRequest request,
            List<Long> horseIds,
            List<Long> itemIds,
            List<FeedSchedItemAmountDTO> items
    ) {
        FeedSchedChangeRequestDTO dto = new FeedSchedChangeRequestDTO();
        dto.setId(request.getId());
        dto.setFeedSchedId(request.getFeedSched().getId());
        dto.setRequestedMorning(request.getRequestedMorning());
        dto.setRequestedNoon(request.getRequestedNoon());
        dto.setRequestedEvening(request.getRequestedEvening());
        dto.setDescription(request.getRequestedDescription() != null
            ? request.getRequestedDescription()
            : request.getFeedSched().getDescription());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setRequestedByName(
            request.getRequestedBy().getUserLname() + " " + request.getRequestedBy().getUserFname()
        );
        dto.setHorseIds(horseIds);
        dto.setItemIds(itemIds);
        dto.setItems(items);
        return dto;
    }
}
