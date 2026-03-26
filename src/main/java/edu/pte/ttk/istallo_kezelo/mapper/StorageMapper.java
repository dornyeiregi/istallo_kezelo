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
        dto.setItemName(storage.getItem().getName());
        applyWarning(storage, dto);
        return dto;
    }

    private static void applyWarning(Storage storage, StorageDTO dto) {
        Double stored = storage.getAmountStored();
        Double inUse = storage.getAmountInUse();
        if (stored == null || inUse == null || inUse <= 0) {
            dto.setDaysRemaining(null);
            dto.setWarningLevel("NONE");
            return;
        }
        int days = (int) Math.floor(stored / inUse);
        dto.setDaysRemaining(days);
        if (days <= 7) {
            dto.setWarningLevel("RED");
        } else if (days <= 14) {
            dto.setWarningLevel("YELLOW");
        } else {
            dto.setWarningLevel("NONE");
        }
    }
}
