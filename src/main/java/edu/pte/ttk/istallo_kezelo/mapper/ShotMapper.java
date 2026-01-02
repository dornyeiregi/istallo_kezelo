package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.ShotDTO;
import edu.pte.ttk.istallo_kezelo.model.Shot;

public final class ShotMapper {
    private ShotMapper() {}

    public static ShotDTO toDTO(Shot shot) {
        ShotDTO dto = new ShotDTO();
        dto.setShotId(shot.getId());
        dto.setShotName(shot.getShotName());
        dto.setDate(shot.getDate());
        dto.setFrequencyUnit(shot.getFrequencyUnit());
        dto.setFrequencyValue(shot.getFrequencyValue());
        dto.setHorseIds(shot.getHorses_treated().stream()
            .map(hs -> hs.getHorse().getId())
            .toList());
        return dto;
    }
}
