package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.StableItemDTO;
import edu.pte.ttk.istallo_kezelo.dto.StableDTO;
import edu.pte.ttk.istallo_kezelo.model.Stable;

/**
 * Mapper segédosztály a(z) Stable konverziókhoz.
 */
public final class StableMapper {

    private StableMapper() {}

    public static StableDTO toDTO(Stable stable) {
        StableDTO dto = new StableDTO();
        dto.setStableId(stable.getId());
        dto.setStableName(stable.getStableName());
        dto.setStrawUsageKg(stable.getStrawUsageKg());
        dto.setStableItems(stable.getStableItems().stream()
            .map(link -> new StableItemDTO(
                link.getItem().getId(),
                link.getUsageKg(),
                link.getItem().getName()
            ))
            .toList());
        dto.setHorses(stable.getHorsesInStable().stream()
            .map(HorseMapper::toDTO)
            .toList());
        return dto;
    }
}
