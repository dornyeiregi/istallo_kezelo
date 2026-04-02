package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.TreatmentDTO;
import edu.pte.ttk.istallo_kezelo.model.Treatment;

/**
 * Mapper segédosztály a(z) Treatment konverziókhoz.
 */
public final class TreatmentMapper {

    private TreatmentMapper() {}

    public static TreatmentDTO toDTO(Treatment treatment) {
        TreatmentDTO dto = new TreatmentDTO();
        dto.setTreatmentId(treatment.getId());
        dto.setTreatmentName(treatment.getTreatmentName());
        dto.setDescription(treatment.getDescription());
        dto.setFrequencyUnit(treatment.getFrequencyUnit());
        dto.setFrequencyValue(treatment.getFrequencyValue());
        dto.setDate(treatment.getDate());
        dto.setHorseIds(treatment.getHorsesTreated().stream()
                .map(link -> link.getHorse().getId())
                .toList());
        return dto;
    }
}
