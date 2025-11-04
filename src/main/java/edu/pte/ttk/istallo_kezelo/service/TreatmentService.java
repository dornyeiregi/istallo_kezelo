package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.dto.TreatmentDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseTreatmentRepository;
import edu.pte.ttk.istallo_kezelo.repository.TreatmentRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;

@Service
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final HorseTreatmentRepository horseTreatmentRepository;
    private final UserRepository userRepository;
    private final HorseRepository horseRepository;

    public TreatmentService(TreatmentRepository treatmentRepository,
                            HorseTreatmentRepository horseTreatmentRepository,
                            UserRepository userRepository,
                            HorseRepository horseRepository) {
        this.treatmentRepository = treatmentRepository;
        this.horseTreatmentRepository = horseTreatmentRepository;
        this.userRepository = userRepository;
        this.horseRepository = horseRepository;
    }

    // Új kezelés létrehozása
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Treatment saveTreatment(Treatment treatment, Authentication auth) {
        if (auth != null && !isAdmin(auth)) {
            for (HorseTreatment ht : treatment.getHorses_treated()) {
                checkHorseOwnership(auth, ht.getHorse().getId());
            }
        }
        return treatmentRepository.save(treatment);
    }

    // Összes kezelés lekérdezése
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<Treatment> getAllTreatments(Authentication auth) {
        List<Treatment> all = treatmentRepository.findAll();
        return filterTreatmentsForOwner(all, auth);
    }

    // Kezelés lekérdezése ID alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Treatment getTreatmentById(Long id, Authentication auth) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kezelés nem található."));

        if (!isAdmin(auth) && treatment.getHorses_treated().stream()
                .noneMatch(link -> link.getHorse().getOwner().getUsername().equals(auth.getName()))) {
            throw new RuntimeException("Nincs jogosultságod ehhez a kezeléshez.");
        }
        return treatment;
    }

    // Egy ló összes kezelése ID alapján
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<Treatment> getTreatmentsByHorseId(Long horseId, Authentication auth) {
        checkHorseOwnership(auth, horseId);
        List<HorseTreatment> treatments = horseTreatmentRepository.findByHorse_Id(horseId);
        return treatments.stream().map(HorseTreatment::getTreatment).toList();
    }

    // Egy ló összes kezelése név alapján
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<Treatment> getTreatmentsByHorseName(String horseName, Authentication auth) {
        Horse horse = horseRepository.findByHorseName(horseName);
        if (horse == null) {
            throw new RuntimeException("Ló nem található.");
        }

        checkHorseOwnership(auth, horse.getId());

        List<HorseTreatment> treatments = horseTreatmentRepository.findByHorse_horseName(horseName);
        return treatments.stream().map(HorseTreatment::getTreatment).toList();
    }

    // Kezelés frissítése
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Treatment updateTreatment(Long treatmentId, TreatmentDTO updatedTreatment, Authentication auth) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new RuntimeException("Kezelés nem található."));

        if (!isAdmin(auth) && treatment.getHorses_treated().stream()
                .noneMatch(link -> link.getHorse().getOwner().getUsername().equals(auth.getName()))) {
            throw new RuntimeException("Csak a saját lovakhoz tartozó kezeléseket módosíthatod.");
        }

        if (updatedTreatment.getTreatmentName() != null) treatment.setTreatmentName(updatedTreatment.getTreatmentName());
        if (updatedTreatment.getDescription() != null) treatment.setDescription(updatedTreatment.getDescription());
        if (updatedTreatment.getDate() != null) treatment.setDate(updatedTreatment.getDate());

        return treatmentRepository.save(treatment);
    }

    // Kezelés törlése
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public void deleteTreatmentById(Long id, Authentication auth) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kezelés nem található."));

        if (!isAdmin(auth)) {
            boolean ownsTreatment = treatment.getHorses_treated().stream()
                    .anyMatch(link -> link.getHorse().getOwner().getUsername().equals(auth.getName()));
            if (!ownsTreatment) {
                throw new RuntimeException("Csak a saját lovakhoz tartozó kezeléseket törölheted.");
            }
        }

        treatmentRepository.deleteById(id);
    }

    // Helper – OWNER csak a saját lovaihoz férhet hozzá
    private void checkHorseOwnership(Authentication auth, Long horseId) {
        if (auth == null || isAdmin(auth)) return;

        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("Ló nem található."));

        if (!horse.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Csak a saját lovaidhoz adhatsz hozzá kezelést.");
        }
    }

    // Helper – OWNER-szűrés minden GET metódushoz
    private List<Treatment> filterTreatmentsForOwner(List<Treatment> all, Authentication auth) {
        if (auth == null || isAdmin(auth)) return all;

        String username = auth.getName();

        return all.stream()
                .filter(treatment -> treatment.getHorses_treated().stream()
                        .anyMatch(link -> link.getHorse().getOwner().getUsername().equals(username)))
                .toList();
    }

    // Helper – ADMIN-e az adott felhasználó
    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
