package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.pte.ttk.istallo_kezelo.dto.StorageDTO;
import edu.pte.ttk.istallo_kezelo.mapper.StorageMapper;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.service.StorageService;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/storages")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService){
        this.storageService = storageService;
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('ADMIN')")
    public StorageDTO addStorage(@RequestBody StorageDTO dto) {
        Storage saved = storageService.createStorage(dto);
        return StorageMapper.toDTO(saved);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<StorageDTO> getAllStorages() {
        return storageService.getAllStorages().stream().map(StorageMapper::toDTO).toList();
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PatchMapping("/{id}")
    public ResponseEntity<StorageDTO> updateStorage(
            @PathVariable Long id,
            @RequestBody StorageDTO dto) {

        Storage updated = storageService.updateStorage(id, dto);
        return ResponseEntity.ok(StorageMapper.toDTO(updated));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<String> deleteStorage(@PathVariable Long id){
        storageService.deleteStorage(id);
        return ResponseEntity.ok("Tároló sikeresen törölve.");
    }

    @PostMapping("/sync")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> syncStorages() {
        storageService.syncAllAmountsInUse();
        return ResponseEntity.ok("Tárolók sikeresen szinkronizálva.");
    }
}
