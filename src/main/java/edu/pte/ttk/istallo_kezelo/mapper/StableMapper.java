package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.StableDTO;
import edu.pte.ttk.istallo_kezelo.model.Stable;

public final class StableMapper {

    private StableMapper() {}

    public static StableDTO toDTO(Stable stable) {
        StableDTO dto = new StableDTO();
        dto.setStableId(stable.getId());
        dto.setStableName(stable.getStableName());
        dto.setHorses(stable.getHorsesInStable().stream()
            .map(HorseMapper::toDTO)
            .toList());
        return dto;
    }
}
