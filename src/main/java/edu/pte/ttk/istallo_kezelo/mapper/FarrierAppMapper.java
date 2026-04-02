package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.FarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.dto.FarrierHorseDTO;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;

/**
 * Mapper segédosztály a(z) FarrierApp konverziókhoz.
 */
public final class FarrierAppMapper {

    /**
     * Statikus segédosztály, példányosítása nem engedélyezett.
     */
    private FarrierAppMapper() {}

    /**
     * FarrierApp entitás DTO-vá alakítása.
     *
     * @param farrierApp patkolás entitás
     * @return DTO
     */
    public static FarrierAppDTO toDTO(FarrierApp farrierApp) {
        return toDTOWithHorseFilter(farrierApp, null);
    }

    /**
     * FarrierApp entitás DTO-vá alakítása tulajdonos nézethez.
     *
     * @param farrierApp    patkolás entitás
     * @param ownerUsername tulajdonos felhasználónév
     * @return DTO
     */
    public static FarrierAppDTO toDTOForOwner(FarrierApp farrierApp, String ownerUsername) {
        return toDTOWithHorseFilter(farrierApp, ownerUsername);
    }

    private static FarrierAppDTO toDTOWithHorseFilter(FarrierApp farrierApp, String ownerUsername) {
        FarrierAppDTO dto = new FarrierAppDTO();
        dto.setFarrierAppId(farrierApp.getId());
        dto.setAppointmentDate(farrierApp.getAppointmentDate());
        dto.setFarrierPhone(farrierApp.getFarrierPhone());
        dto.setFarrierName(farrierApp.getFarrierName());
        dto.setFrequencyUnit(farrierApp.getFrequencyUnit());
        dto.setFrequencyValue(farrierApp.getFrequencyValue());
        dto.setShoes(farrierApp.getShoes());
        var filtered = farrierApp.getHorses_done().stream()
                .filter(hfa -> ownerUsername == null
                        || hfa.getHorse().getOwner().getUsername().equals(ownerUsername));
        dto.setHorseIds(filtered
                .map(hfa -> hfa.getHorse().getId())
                .toList());
        dto.setHorseDetails(
            farrierApp.getHorses_done().stream()
                .filter(hfa -> ownerUsername == null
                        || hfa.getHorse().getOwner().getUsername().equals(ownerUsername))
                .map(hfa -> new FarrierHorseDTO(
                        hfa.getHorse().getId(),
                        hfa.getHorse().getHorseName(),
                        hfa.getShoeCount(),
                        hfa.getNote()
                ))
                .toList()
        );
        return dto;
    }
}
