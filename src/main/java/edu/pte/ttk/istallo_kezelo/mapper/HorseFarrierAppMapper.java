package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.HorseFarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;

public final class HorseFarrierAppMapper {

    private HorseFarrierAppMapper() {}

    public static HorseFarrierAppDTO toDTO(HorseFarrierApp link) {
        HorseFarrierAppDTO dto = new HorseFarrierAppDTO();
        dto.setHorseId(link.getHorse().getId());
        dto.setFarrierAppId(link.getFarrierApp().getId());
        dto.setHorseName(link.getHorse().getHorseName());
        dto.setAppointmentDate(link.getFarrierApp().getAppointmentDate());
        dto.setShoes(link.getFarrierApp().getShoes());
        return dto;
    }
}
