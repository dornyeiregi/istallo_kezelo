package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.repository.FarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;

@Service
public class HorseFarrierAppService {
    private final FarrierAppRepository farrierAppRepository;
    private final HorseRepository horseRepository;
    private final HorseFarrierAppRepository horseFarrierAppRepository;
    private final UserRepository userRepository;
    private final CalendarEventService calendarEventService;

    public HorseFarrierAppService(FarrierAppRepository farrierAppRepository, 
                                  HorseRepository horseRepository, 
                                  HorseFarrierAppRepository horseFarrierAppRepository,
                                  UserRepository userRepository,
                                  CalendarEventService calendarEventService) {
        this.farrierAppRepository = farrierAppRepository;
        this.horseRepository = horseRepository;
        this.horseFarrierAppRepository = horseFarrierAppRepository;
        this.userRepository = userRepository;
        this.calendarEventService = calendarEventService;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public HorseFarrierApp addHorseToFarrierApp(Long farrierAppId, Long horseId, Authentication auth){
        checkHorseOwnership(auth, horseId);
        FarrierApp app = farrierAppRepository.findById(farrierAppId)
            .orElseThrow(() -> new RuntimeException("Patkolás nem található."));
        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));
        boolean exists = horseFarrierAppRepository.existsByFarrierAppAndHorse(app, horse);
        if (exists) {
            throw new RuntimeException("A ló már hozzá van csatolva a patkoláshoz.");
        }
        HorseFarrierApp link = new HorseFarrierApp();
        link.setFarrierApp(app);
        link.setHorse(horse);
        HorseFarrierApp saved = horseFarrierAppRepository.save(link);
        calendarEventService.syncFromDomain(
                horse,
                EventType.FARRIERAPP,
                app.getAppointmentDate(),
                app.getId()
        );
        return saved;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseFarrierApp> getAllHorseFarrierApps(Authentication auth){
        List<HorseFarrierApp> all = horseFarrierAppRepository.findAll();
        return filterHorseFarrierAppsForOwner(all, auth);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public void removeHorseFromFarrierApp(Long farrierAppId, Long horseId, Authentication auth){
        checkHorseOwnership(auth, horseId);
        horseFarrierAppRepository.deleteByFarrierApp_IdAndHorse_Id(farrierAppId, horseId);
        calendarEventService.deleteFromDomain(EventType.FARRIERAPP, farrierAppId, horseId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseFarrierApp> getHorsesForFarrierApp(Long farrierAppId, Authentication auth){
        List<HorseFarrierApp> all = horseFarrierAppRepository.findByFarrierApp_Id(farrierAppId);
        return filterHorseFarrierAppsForOwner(all, auth);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseFarrierApp> getFarrierAppsForHorse(Long horseId, Authentication auth){
        checkHorseOwnership(auth, horseId);
        return horseFarrierAppRepository.findByHorseId(horseId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public HorseFarrierApp getHorseFarrierAppById(Long id, Authentication auth){
        HorseFarrierApp link = horseFarrierAppRepository.findById(id)
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
            throw new RuntimeException("Csak a saját lovadhoz tartozó patkolást érheted el.");
        }
        return link;
    }

    private void checkHorseOwnership(Authentication auth, Long horseId) {
        if (auth == null) { return; }
        String username = auth.getName();
        User currentuser = userRepository.findByUsername(username);
        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !horse.getOwner().getId().equals(currentuser.getId())) {
            throw new RuntimeException("Csak saját lovaidhoz adhatsz vagy törölhetsz patkolást.");
        }
    }

    private List<HorseFarrierApp> filterHorseFarrierAppsForOwner(List<HorseFarrierApp> all, Authentication auth) {
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
