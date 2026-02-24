package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.dto.TreatmentDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
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
    private final CalendarEventService calendarEventService;

    public TreatmentService(TreatmentRepository treatmentRepository,
                            HorseTreatmentRepository horseTreatmentRepository,
                            UserRepository userRepository,
                            HorseRepository horseRepository,
                            CalendarEventService calendarEventService) {
        this.treatmentRepository = treatmentRepository;
        this.horseTreatmentRepository = horseTreatmentRepository;
        this.userRepository = userRepository;
        this.horseRepository = horseRepository;
        this.calendarEventService = calendarEventService;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Treatment saveTreatment(Treatment treatment, List<Long> horseIds, Authentication auth) {
        Treatment saved = treatmentRepository.save(treatment);
        if (horseIds != null) {
            for (Long horseId : horseIds) {
                if (auth != null && !isAdmin(auth)) {
                    checkHorseOwnership(auth, horseId);
                }
                Horse horse = horseRepository.findById(horseId)
                        .orElseThrow(() -> new RuntimeException("Ló nem található: " + horseId));

                HorseTreatment link = new HorseTreatment();
                link.setHorse(horse);
                link.setTreatment(saved);
                horseTreatmentRepository.save(link);
                saved.getHorsesTreated().add(link);
                calendarEventService.syncFromDomain(
                        horse,
                        EventType.TREATMENT,
                        saved.getDate(),
                        saved.getId()
                );
            }
        }
        return saved;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<Treatment> getAllTreatments(Authentication auth) {
        List<Treatment> all = treatmentRepository.findAll();
        return filterTreatmentsForOwner(all, auth);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Treatment getTreatmentById(Long id, Authentication auth) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kezelés nem található."));
        if (!isAdmin(auth) && treatment.getHorsesTreated().stream()
                .noneMatch(link -> link.getHorse().getOwner().getUsername().equals(auth.getName()))) {
            throw new RuntimeException("Nincs jogosultságod ehhez a kezeléshez.");
        }
        return treatment;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<Treatment> getTreatmentsByHorseId(Long horseId, Authentication auth) {
        checkHorseOwnership(auth, horseId);
        List<HorseTreatment> treatments = horseTreatmentRepository.findByHorse_Id(horseId);
        return treatments.stream().map(HorseTreatment::getTreatment).toList();
    }

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

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Treatment updateTreatment(Long treatmentId, TreatmentDTO updatedTreatment, Authentication auth) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new RuntimeException("Kezelés nem található."));
        if (!isAdmin(auth) && treatment.getHorsesTreated().stream()
                .noneMatch(link -> link.getHorse().getOwner().getUsername().equals(auth.getName()))) {
            throw new RuntimeException("Csak a saját lovakhoz tartozó kezeléseket módosíthatod.");
        }
        if (updatedTreatment.getTreatmentName() != null) treatment.setTreatmentName(updatedTreatment.getTreatmentName());
        if (updatedTreatment.getDescription() != null) treatment.setDescription(updatedTreatment.getDescription());
        if (updatedTreatment.getDate() != null) treatment.setDate(updatedTreatment.getDate());
        if (updatedTreatment.getHorseIds() != null) {
            Set<Long> desiredHorseIds = new HashSet<>(updatedTreatment.getHorseIds());
            Set<Long> existingHorseIds = treatment.getHorsesTreated().stream()
                    .map(link -> link.getHorse().getId())
                    .collect(java.util.stream.Collectors.toSet());
            java.util.Iterator<HorseTreatment> iterator = treatment.getHorsesTreated().iterator();
            while (iterator.hasNext()) {
                HorseTreatment link = iterator.next();
                Long horseId = link.getHorse().getId();
                if (!desiredHorseIds.contains(horseId)) {
                    iterator.remove();
                    calendarEventService.deleteFromDomain(EventType.TREATMENT, treatmentId, horseId);
                    horseTreatmentRepository.delete(link);
                }
            }
            for (Long horseId : desiredHorseIds) {
                if (!existingHorseIds.contains(horseId)) {
                    if (auth != null && !isAdmin(auth)) {
                        checkHorseOwnership(auth, horseId);
                    }
                    Horse horse = horseRepository.findById(horseId)
                            .orElseThrow(() -> new RuntimeException("Ló nem található: " + horseId));
                    HorseTreatment link = new HorseTreatment();
                    link.setHorse(horse);
                    link.setTreatment(treatment);
                    treatment.getHorsesTreated().add(link);
                }
            }
        }
        Treatment saved = treatmentRepository.save(treatment);
        for (HorseTreatment link : saved.getHorsesTreated()) {
            Horse horse = link.getHorse();
            calendarEventService.syncFromDomain(
                    horse,
                    EventType.TREATMENT,
                    saved.getDate(),
                    saved.getId()
            );
        }
        return saved;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public void deleteTreatmentById(Long id, Authentication auth) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kezelés nem található."));
        if (!isAdmin(auth)) {
            boolean ownsTreatment = treatment.getHorsesTreated().stream()
                    .anyMatch(link -> link.getHorse().getOwner().getUsername().equals(auth.getName()));
            if (!ownsTreatment) {
                throw new RuntimeException("Csak a saját lovakhoz tartozó kezeléseket törölheted.");
            }
        }
        treatmentRepository.deleteById(id);
        calendarEventService.deleteFromDomain(EventType.TREATMENT, id);
    }

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

    private List<Treatment> filterTreatmentsForOwner(List<Treatment> all, Authentication auth) {
        if (auth == null || isAdmin(auth)) return all;
        String username = auth.getName();
        return all.stream()
                .filter(treatment -> treatment.getHorsesTreated().stream()
                        .anyMatch(link -> link.getHorse().getOwner().getUsername().equals(username)))
                .toList();
    }

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
