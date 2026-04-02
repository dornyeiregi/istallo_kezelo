package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.HorseTreatmentDTO;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;

/**
 * Mapper segédosztály a(z) HorseTreatment konverziókhoz.
 */
public final class HorseTreatmentMapper {

    private HorseTreatmentMapper() {}

    public static HorseTreatmentDTO toDTO(HorseTreatment link) {
        HorseTreatmentDTO dto = new HorseTreatmentDTO();
        dto.setHorseId(link.getHorse().getId());
        dto.setTreatmentId(link.getTreatment().getId());
        dto.setTreatmentName(link.getTreatment().getTreatmentName());
        dto.setHorseName(link.getHorse().getHorseName());
        dto.setDate(link.getTreatment().getDate());
        return dto;
    }
}
