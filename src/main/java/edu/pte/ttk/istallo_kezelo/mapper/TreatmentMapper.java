package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.TreatmentDTO;
import edu.pte.ttk.istallo_kezelo.model.Treatment;

public final class TreatmentMapper {
    private TreatmentMapper() {}

    public static TreatmentDTO toDTO(Treatment treatment) {
        TreatmentDTO dto = new TreatmentDTO();
        dto.setTreatmentId(treatment.getId());
        dto.setTreatmentName(treatment.getTreatmentName());
        dto.setDescription(treatment.getDescription());
        dto.setDate(treatment.getDate());
        return dto;
    }
}
