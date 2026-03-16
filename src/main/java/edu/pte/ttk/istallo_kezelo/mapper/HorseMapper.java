package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.HorseDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;

public final class HorseMapper {

    private HorseMapper() {}

    public static HorseDTO toDTO(Horse horse){
        HorseDTO dto = new HorseDTO();
        dto.setId(horse.getId());
        dto.setHorseName(horse.getHorseName());
        dto.setDob(horse.getDob());
        dto.setSex(horse.getSex());
        dto.setOwnerName(horse.getOwner().getUserLname() + " "
            + horse.getOwner().getUserFname());
        dto.setOwnerId(horse.getOwner().getId());
        if (horse.getStable() != null) {
            dto.setStableName(horse.getStable().getStableName());
            dto.setStableId(horse.getStable().getId());
        } else {
            dto.setStableName(null);
            dto.setStableId(null);
        }
        dto.setMicrochipNum(horse.getMicrochipNum());
        dto.setPassportNum(horse.getPassportNum());
        dto.setAdditional(horse.getAdditional());
        dto.setIsActive(horse.getIsActive());
        return dto;
    }
}
