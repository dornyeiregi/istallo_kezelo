package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseTreatmentRepository;
import edu.pte.ttk.istallo_kezelo.repository.TreatmentRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;

/**
 * Application service for linking horses to treatments.
 */
@Service
public class HorseTreatmentService {
    private final HorseTreatmentRepository horseTreatmentRepository;
    private final HorseRepository horseRepository;
    private final TreatmentRepository treatmentRepository;
    private final UserRepository userRepository;
    private final CalendarEventService calendarEventService;

    public HorseTreatmentService(HorseTreatmentRepository horseTreatmentRepository,
                                HorseRepository horseRepository,
                                TreatmentRepository treatmentRepository,
                                UserRepository userRepository,
                                CalendarEventService calendarEventService){
        this.horseTreatmentRepository = horseTreatmentRepository;
        this.horseRepository = horseRepository;
        this.treatmentRepository = treatmentRepository;
        this.userRepository = userRepository;
        this.calendarEventService = calendarEventService;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public HorseTreatment addTreatmentToHorse(Long treatmentId, Long horseId, Authentication auth){
        checkHorseOwnership(auth, horseId);
        Treatment treatment = treatmentRepository.findById(treatmentId)
            .orElseThrow(() -> new RuntimeException("Kezelés nem található."));
        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));
        boolean exists = horseTreatmentRepository.existsByTreatmentAndHorse(treatment, horse);
        if (exists) {
            throw new RuntimeException("A kezelés már hozzá van adva a lóhoz.");
        }
        HorseTreatment link = new HorseTreatment();
        link.setHorse(horse);
        link.setTreatment(treatment);
        HorseTreatment saved = horseTreatmentRepository.save(link);
        calendarEventService.syncFromDomain(
                horse,
                EventType.TREATMENT,
                treatment.getDate(),
                treatment.getId()
        );
        return saved;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Transactional(readOnly = true)
    public List<HorseTreatment> getAllHorseTreatments(Authentication auth){
        List<HorseTreatment> all = horseTreatmentRepository.findAll();
        return filterHorseTreatmentForOwner(all, auth);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public HorseTreatment getHorseTreatmentById(Long id, Authentication auth){
        HorseTreatment link = horseTreatmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Link nem található."));
        if (auth == null) {
            return link;
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return link;
        }
        String username = auth.getName();
        if (!link.getHorse().getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Csak a saját lovaidhoz tartozó kezeléseket érheted el.");
        }
        return link;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseTreatment> getTreatmentsForHorse(Long horseId, Authentication auth){
        List<HorseTreatment> all = horseTreatmentRepository.findByHorse_Id(horseId);
        return filterHorseTreatmentForOwner(all, auth);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseTreatment> getHorsesByTreatment(Long treatmentId, Authentication auth) {
        List<HorseTreatment> all =  horseTreatmentRepository.findByTreatment_Id(treatmentId);
        return filterHorseTreatmentForOwner(all, auth);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public void removeTreatmentFromHorse(Long treatmentId, Long horseId, Authentication auth){
        checkHorseOwnership(auth, horseId);
        horseTreatmentRepository.deleteByTreatment_IdAndHorse_Id(treatmentId, horseId);
        calendarEventService.deleteFromDomain(EventType.TREATMENT, treatmentId, horseId);
    }

    private void checkHorseOwnership(Authentication auth, Long horseId) {
        if (auth == null) return;
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("Ló nem található."));
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !horse.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Csak a saját lovaidhoz adhatsz vagy törölhetsz oltásokat.");
        }
    }

    private List<HorseTreatment> filterHorseTreatmentForOwner(List<HorseTreatment> all, Authentication auth) {
        if (auth == null) return all;
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return all;
        String username = auth.getName();
        return all.stream()
                .filter(link -> link.getHorse().getOwner().getUsername().equals(username))
                .toList();
    }
}
