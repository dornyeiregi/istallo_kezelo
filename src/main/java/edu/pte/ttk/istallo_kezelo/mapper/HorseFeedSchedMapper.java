package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.HorseFeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;

public final class HorseFeedSchedMapper {

    private HorseFeedSchedMapper() {}

    public static HorseFeedSchedDTO toDTO(HorseFeedSched horseFeedSched) {
        HorseFeedSchedDTO dto = new HorseFeedSchedDTO();
        dto.setHorseId(horseFeedSched.getHorse().getId());
        dto.setFeedSchedId(horseFeedSched.getFeedSched().getId());
        dto.setFeedDescription(horseFeedSched.getFeedSched().getDescription());
        dto.setHorseName(horseFeedSched.getHorse().getHorseName());
        return dto;
    }
}
