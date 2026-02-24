package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.FarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;

public final class FarrierAppMapper {

    private FarrierAppMapper() {}

    public static FarrierAppDTO toDTO(FarrierApp farrierApp) {
        FarrierAppDTO dto = new FarrierAppDTO();
        dto.setFarrierAppId(farrierApp.getId());
        dto.setAppointmentDate(farrierApp.getAppointmentDate());
        dto.setFarrierPhone(farrierApp.getFarrierPhone());
        dto.setFarrierName(farrierApp.getFarrierName());
        dto.setShoes(farrierApp.getShoes());
        dto.setHorseIds(farrierApp.getHorses_done().stream()
                .map(hfa -> hfa.getHorse().getId()).toList());
        return dto;
    }
}
