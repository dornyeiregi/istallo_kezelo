package edu.pte.ttk.istallo_kezelo.mapper;

import java.util.List;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedChangeRequestDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedChangeRequest;

public final class FeedSchedChangeRequestMapper {

    private FeedSchedChangeRequestMapper() {}

    public static FeedSchedChangeRequestDTO toDTO(
            FeedSchedChangeRequest request,
            List<Long> horseIds,
            List<Long> itemIds
    ) {
        FeedSchedChangeRequestDTO dto = new FeedSchedChangeRequestDTO();
        dto.setId(request.getId());
        dto.setFeedSchedId(request.getFeedSched().getId());
        dto.setRequestedMorning(request.getRequestedMorning());
        dto.setRequestedNoon(request.getRequestedNoon());
        dto.setRequestedEvening(request.getRequestedEvening());
        dto.setDescription(request.getFeedSched().getDescription());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setRequestedByName(
            request.getRequestedBy().getUserLname() + " " + request.getRequestedBy().getUserFname()
        );
        dto.setHorseIds(horseIds);
        dto.setItemIds(itemIds);
        return dto;
    }
}
