package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.StorageDTO;
import edu.pte.ttk.istallo_kezelo.model.Storage;

public final class StorageMapper {
    private StorageMapper() {}

    public static StorageDTO toDTO(Storage storage) {
        StorageDTO dto = new StorageDTO();
        dto.setStorageId(storage.getId());
        dto.setAmountInUse(storage.getAmountInUse());
        dto.setAmountStored(storage.getAmountStored());
        dto.setItemId(storage.getItem().getId());
        return dto;
    }
}
