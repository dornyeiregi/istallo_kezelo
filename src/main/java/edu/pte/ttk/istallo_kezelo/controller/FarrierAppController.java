package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.pte.ttk.istallo_kezelo.dto.FarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.mapper.FarrierAppMapper;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.service.FarrierAppService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Patkolási időpontok végpontjai.
 */
@RestController
@RequestMapping("/api/farrierApps")
public class FarrierAppController {

    private final FarrierAppService farrierAppService;

    /**
     * Létrehozza a vezérlőt a szükséges szolgáltatással.
     *
     * @param farrierAppService patkolás szolgáltatás
     */
    public FarrierAppController(FarrierAppService farrierAppService) {
        this.farrierAppService = farrierAppService;
    }

    /**
     * Új patkolási időpont létrehozása.
     *
     * @param dto  patkolás adatai
     * @param auth hitelesítési adatok
     * @return létrehozott patkolás DTO
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<FarrierAppDTO> createFarrierApp(@RequestBody FarrierAppDTO dto, Authentication auth) {
        FarrierApp created = farrierAppService.createFarrierApp(dto, auth);
        return ResponseEntity.ok(FarrierAppMapper.toDTO(created));
    }

    /**
     * Patkolások listázása a jogosultságoknak megfelelően.
     *
     * @param auth hitelesítési adatok
     * @return patkolások listája
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public List<FarrierAppDTO> getAllFarrierApps(Authentication auth) {
        List<FarrierApp> farrierApps = farrierAppService.getAllFarrierApps(auth);
        return farrierApps.stream().map(app -> toDTOForAuth(app, auth)).toList();
    }

    /**
     * Patkolás lekérése azonosító alapján.
     *
     * @param id   patkolás azonosító
     * @param auth hitelesítési adatok
     * @return patkolás DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public FarrierAppDTO getFarrierAppById(@PathVariable Long id, Authentication auth) {
        FarrierApp farrierApp = farrierAppService.getFarrierAppById(id, auth);
        if (farrierApp == null) {
            throw new RuntimeException("Patkolás nem található.");
        }
        return toDTOForAuth(farrierApp, auth);
    }

    /**
     * Patkolások lekérése ló azonosító alapján.
     *
     * @param horseId ló azonosító
     * @param auth    hitelesítési adatok
     * @return patkolások listája
     */
    @GetMapping("/horseId/{horseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public List<FarrierAppDTO> getFarrierAppsByHorseId(@PathVariable Long horseId, Authentication auth) {
        List<FarrierApp> farrierApps = farrierAppService.getFarrierAppByHorseId(horseId, auth);
        return farrierApps.stream().map(app -> toDTOForAuth(app, auth)).toList();
    }

    /**
     * Patkolás adatainak frissítése.
     *
     * @param id   patkolás azonosító
     * @param dto  módosítási adatok
     * @param auth hitelesítési adatok
     * @return státusz üzenet
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<String> updateFarrierApp(@PathVariable Long id, @RequestBody FarrierAppDTO dto, Authentication auth) {
        farrierAppService.updateFarrierApp(id, dto, auth);
        return ResponseEntity.ok("Patkolás sikeresen frissítve.");
    }
    
    /**
     * Patkolás törlése azonosító alapján.
     *
     * @param id patkolás azonosító
     * @return státusz üzenet
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> deleteFarrierApp(@PathVariable Long id) {
        farrierAppService.deleteFarrierApp(id);
        return ResponseEntity.ok("Patkolás sikeresen törölve.");
    }

    private FarrierAppDTO toDTOForAuth(FarrierApp farrierApp, Authentication auth) {
        if (auth == null) {
            return FarrierAppMapper.toDTO(farrierApp);
        }
        boolean isAdminOrEmployee = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_EMPLOYEE"));
        if (isAdminOrEmployee) {
            return FarrierAppMapper.toDTO(farrierApp);
        }
        return FarrierAppMapper.toDTOForOwner(farrierApp, auth.getName());
    }
}
