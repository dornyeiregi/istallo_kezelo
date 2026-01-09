package edu.pte.ttk.istallo_kezelo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.controller.AuthController;
import edu.pte.ttk.istallo_kezelo.dto.FarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.repository.FarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;

@Service
public class FarrierAppService {


    private final FarrierAppRepository farrierAppRepository;
    private final HorseRepository horseRepository;
    private final UserRepository userRepository;
    private final CalendarEventService calendarEventService;

    public FarrierAppService(FarrierAppRepository farrierAppRepository,
                             HorseRepository horseRepository,
                             UserRepository userRepository,
                             AuthController authController,
                             CalendarEventService calendarEventService) {
        this.farrierAppRepository = farrierAppRepository; 
        this.horseRepository = horseRepository;
        this.userRepository = userRepository;
        this.calendarEventService = calendarEventService;
    }
    
    // Új patkolás hozzáadása
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public FarrierApp createFarrierApp(FarrierAppDTO dto, Authentication auth) {
        FarrierApp farrierApp = new FarrierApp();
        farrierApp.setAppointmentDate(dto.getAppointmentDate());
        farrierApp.setFarrierName(dto.getFarrierName());
        farrierApp.setFarrierPhone(dto.getFarrierPhone());
        farrierApp.setShoes(dto.getShoes());

        farrierApp = farrierAppRepository.save(farrierApp);

        if (dto.getHorseIds() != null) {
            for (Long horseId : dto.getHorseIds()) {
                checkHorseOwnership(auth, horseId);
                addHorseToFarrierApp(farrierApp.getId(), horseId);
            }
        }

        return farrierApp;
    }

    // Összes patkolás lekérdezése
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<FarrierApp> getAllFarrierApps(Authentication auth) {
        List<FarrierApp> all = farrierAppRepository.findAll();
        return filterFarrierAppsForOwner(all, auth);
    }

    // Patkolás lekérdezése id alaján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public FarrierApp getFarrierAppById(Long id, Authentication auth) {
        FarrierApp app = farrierAppRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patkolás nem található."));

        if (!isAdmin(auth) && app.getHorses_done().stream()
            .noneMatch(link -> link.getHorse().getOwner().getUsername().equals(auth.getName()))) {
                throw new RuntimeException("Nincs jogosultságod ehhez a patkolás megetkintéséhez.");
        }
        return app;
    }

    // Patkolás lekérdezése dátum alaján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<FarrierApp> getFarrierAppsByDate(LocalDate date, Authentication auth) {
        List<FarrierApp> all = farrierAppRepository.findByAppointmentDate(date);
        return filterFarrierAppsForOwner(all, auth);
    }

    // Patkolás lekérdezése patkolókovács neve alaján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<FarrierApp> getFarrierAppsByFarrierName(String farrierName, Authentication auth) {
        List<FarrierApp> all = farrierAppRepository.findByFarrierName(farrierName);
        return filterFarrierAppsForOwner(all, auth);
    }

    // Patkolás lekérdezése ló neve alaján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<FarrierApp> getFarrierAppsByHorseName(String horseName, Authentication auth) {
        List<FarrierApp> all = farrierAppRepository.findByHorsesDone_Horse_HorseName(horseName);
        return filterFarrierAppsForOwner(all, auth);
    }

    // Patkolás lekérdezése ló id alaján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<FarrierApp> getFarrierAppByHorseId(Long horseId, Authentication auth) {
        List<FarrierApp> all = farrierAppRepository.findAll().stream()
                .filter(app -> app.getHorses_done().stream()
                        .anyMatch(horseApp -> horseApp.getHorse().getId().equals(horseId)))
                .toList();
        return filterFarrierAppsForOwner(all, auth);
    }


    // Patkolás frissítése
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public void updateFarrierApp(Long id, FarrierAppDTO dto, Authentication auth) {
        FarrierApp existingFarrierApp = farrierAppRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patkolás nem található"));
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("OWNER"))) {
            String username = auth.getName();

            if (dto.getHorseIds() != null) {
                for (Long horseId : dto.getHorseIds()) {
                    Horse horse = horseRepository.findById(horseId)
                        .orElseThrow(() -> new RuntimeException("Ló nem található."));

                    if (!horse.getOwner().getUsername().equals(username)) {
                        throw new RuntimeException("Csak saját lovakhoz lehet hozzáadni patkolást.");
                    }
                }
            }
        }
        if(dto.getAppointmentDate() != null) {
            existingFarrierApp.setAppointmentDate(dto.getAppointmentDate());
        }
        if(dto.getFarrierName() != null) {
            existingFarrierApp.setFarrierName(dto.getFarrierName());
        }
        if(dto.getFarrierPhone() != null) {
            existingFarrierApp.setFarrierPhone(dto.getFarrierPhone());
        }
        if(dto.getShoes() != null) {
            existingFarrierApp.setShoes(dto.getShoes());
        }
        if(dto.getHorseIds() != null) {
            existingFarrierApp.getHorses_done().clear();

            for(Long horseId : dto.getHorseIds()) {
                Horse horse = horseRepository.findById(horseId)
                    .orElseThrow(() -> new RuntimeException("Ló nem található"));

                HorseFarrierApp link = new HorseFarrierApp();
                link.setHorse(horse);
                link.setFarrierApp(existingFarrierApp);
                existingFarrierApp.getHorses_done().add(link);
            }
        }
        farrierAppRepository.save(existingFarrierApp);

        calendarEventService.deleteFromDomain(EventType.FARRIERAPP, existingFarrierApp.getId());
        for (HorseFarrierApp link : existingFarrierApp.getHorses_done()) {
            Horse horse = link.getHorse();
            calendarEventService.createEvent(
                    horse.getId(),
                    EventType.FARRIERAPP,
                    existingFarrierApp.getAppointmentDate(),
                    existingFarrierApp.getId()
            );
        }

    }

    // Patkolás törlése
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteFarrierApp(Long id) {
        farrierAppRepository.deleteById(id);
        calendarEventService.deleteFromDomain(EventType.FARRIERAPP, id);
    }

    // Ló csatolása patkoláshoz
    @Transactional
    public void addHorseToFarrierApp(Long id, Long horseId) {
        FarrierApp farrierApp = farrierAppRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patkolás nem található"));
        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("Ló nem található"));

        HorseFarrierApp link = new HorseFarrierApp();
        link.setHorse(horse);
        link.setFarrierApp(farrierApp);
        farrierApp.getHorses_done().add(link);
        farrierAppRepository.save(farrierApp);
        calendarEventService.createEvent(
                horse.getId(),
                EventType.FARRIERAPP,
                farrierApp.getAppointmentDate(),
                farrierApp.getId()
        );
    }

    // Owner csak saját lovához férhessen hozzá
    private void checkHorseOwnership(Authentication auth, Long horseId) {
        if (auth == null) return;

        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("Ló nem található"));

        if (!isAdmin(auth) && !horse.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Csak a saját lovaidhoz adhatsz hozzá patkolást.");
        }
    }

    // Szűrés minden GET metódusra az OWNER felhasználóknak
    private List<FarrierApp> filterFarrierAppsForOwner(List<FarrierApp> allApps, Authentication auth) {
        if (auth == null) return allApps;
        String username = auth.getName();

        if (isAdmin(auth)) {
            return allApps;
        }

        return allApps.stream().filter(app -> app.getHorses_done().stream()
            .anyMatch(link -> link.getHorse().getOwner().getUsername().equals(username))).toList();
    }

        // Helper: ADMIN-e az adott felhasználó
    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }


}
