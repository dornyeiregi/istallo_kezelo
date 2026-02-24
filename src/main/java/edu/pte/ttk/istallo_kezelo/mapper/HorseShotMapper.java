package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.HorseShotDTO;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;

public final class HorseShotMapper {

    private HorseShotMapper() {}

    public static HorseShotDTO toDTO(HorseShot link) {
        HorseShotDTO dto = new HorseShotDTO();
        dto.setShotId(link.getShot().getId());
        dto.setHorseId(link.getHorse().getId());
        dto.setHorseName(link.getHorse().getHorseName());
        dto.setShotName(link.getShot().getShotName());
        dto.setDate(link.getShot().getDate());
        dto.setFrequencyUnit(link.getShot().getFrequencyUnit());
        dto.setFrequencyValue(link.getShot().getFrequencyValue());
        return dto;
    }
}
