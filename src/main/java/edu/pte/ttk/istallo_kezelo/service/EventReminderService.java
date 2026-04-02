package edu.pte.ttk.istallo_kezelo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.NotificationLog;
import edu.pte.ttk.istallo_kezelo.repository.HorseFarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseTreatmentRepository;
import edu.pte.ttk.istallo_kezelo.repository.NotificationLogRepository;

/**
 * Ütemezett szolgáltatás a közelgő események emlékeztető e-mailjeihez.
 */
@Service
public class EventReminderService {

    private final HorseFarrierAppRepository horseFarrierAppRepository;
    private final HorseShotRepository horseShotRepository;
    private final HorseTreatmentRepository horseTreatmentRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final MailService mailService;
    private final List<Integer> reminderDays;
    private final String farrierSubject;
    private final String farrierBody;
    private final String shotSubject;
    private final String shotBody;
    private final String treatmentSubject;
    private final String treatmentBody;

    /**
     * Létrehozza a szolgáltatást és beolvassa az e-mail sablonokat.
     *
     * @param horseFarrierAppRepository patkolás kapcsolatok repository
     * @param horseShotRepository       oltás kapcsolatok repository
     * @param horseTreatmentRepository  kezelés kapcsolatok repository
     * @param notificationLogRepository értesítési napló repository
     * @param mailService               e-mail küldő szolgáltatás
     * @param reminderDays              emlékeztető napok (pl. "7,2")
     * @param farrierSubject            patkolás tárgy sablon
     * @param farrierBody               patkolás törzs sablon
     * @param shotSubject               oltás tárgy sablon
     * @param shotBody                  oltás törzs sablon
     * @param treatmentSubject          kezelés tárgy sablon
     * @param treatmentBody             kezelés törzs sablon
     */
    public EventReminderService(HorseFarrierAppRepository horseFarrierAppRepository,
                                HorseShotRepository horseShotRepository,
                                HorseTreatmentRepository horseTreatmentRepository,
                                NotificationLogRepository notificationLogRepository,
                                MailService mailService,
                                @Value("${app.mail.reminder.days:7,2}") String reminderDays,
                                @Value("${app.mail.reminder.farrier.subject:Közelgő patkolás}") String farrierSubject,
                                @Value("${app.mail.reminder.farrier.body:{horse} - Patkolás esedékes {date} ({days} nap múlva).}") String farrierBody,
                                @Value("${app.mail.reminder.shot.subject:Közelgő oltás}") String shotSubject,
                                @Value("${app.mail.reminder.shot.body:{horse} - Oltás esedékes {date} ({days} nap múlva).}") String shotBody,
                                @Value("${app.mail.reminder.treatment.subject:Közelgő kezelés}") String treatmentSubject,
                                @Value("${app.mail.reminder.treatment.body:{horse} - Kezelés esedékes {date} ({days} nap múlva).}") String treatmentBody) {
        this.horseFarrierAppRepository = horseFarrierAppRepository;
        this.horseShotRepository = horseShotRepository;
        this.horseTreatmentRepository = horseTreatmentRepository;
        this.notificationLogRepository = notificationLogRepository;
        this.mailService = mailService;
        this.reminderDays = parseDays(reminderDays);
        this.farrierSubject = farrierSubject;
        this.farrierBody = farrierBody;
        this.shotSubject = shotSubject;
        this.shotBody = shotBody;
        this.treatmentSubject = treatmentSubject;
        this.treatmentBody = treatmentBody;
    }

    /**
     * Ütemezett futás: emlékeztetők kiküldése a mai nap alapján.
     */
    @Scheduled(cron = "${app.mail.reminder.cron:0 5 7 * * *}")
    @Transactional
    public void sendReminders() {
        runReminders(false);
    }

    /**
     * Azonnali emlékeztetők küldése (pl. esemény létrehozásakor).
     */
    @Transactional
    public void sendRemindersNow() {
        runReminders(false);
    }

    /**
     * Induláskor futó emlékeztető (catch-up üzemmódban).
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void sendRemindersOnStartup() {
        runReminders(true);
    }

    private void runReminders(boolean catchUp) {
        if (!mailService.isEnabled()) return;
        LocalDate today = LocalDate.now();
        notifyFarrier(today, catchUp);
        notifyShots(today, catchUp);
        notifyTreatments(today, catchUp);
    }

    private void notifyFarrier(LocalDate today, boolean catchUp) {
        List<HorseFarrierApp> links = horseFarrierAppRepository.findAll();
        for (HorseFarrierApp link : links) {
            if (link.getFarrierApp() == null || link.getHorse() == null) continue;
            LocalDate date = link.getFarrierApp().getAppointmentDate();
            if (date == null) continue;
            int days = (int) ChronoUnit.DAYS.between(today, date);
            for (int targetDays : reminderDays) {
                if (!shouldSend(days, targetDays, catchUp)) continue;
                if (alreadySent("FARRIER", link.getFarrierApp().getId(), link.getHorse().getId(), targetDays)) continue;
                String email = link.getHorse().getOwner() != null ? link.getHorse().getOwner().getEmail() : null;
                if (email == null || email.isBlank()) continue;
                String subject = farrierSubject;
                String body = render(farrierBody, link.getHorse().getHorseName(), date, targetDays);
                mailService.sendToRecipients(List.of(email.trim()), subject, body);
                saveLog("FARRIER", link.getFarrierApp().getId(), link.getHorse().getId(), targetDays);
            }
        }
    }

    private void notifyShots(LocalDate today, boolean catchUp) {
        List<HorseShot> links = horseShotRepository.findAll();
        for (HorseShot link : links) {
            if (link.getShot() == null || link.getHorse() == null) continue;
            LocalDate date = resolveNextDate(
                link.getShot().getDate(),
                link.getShot().getFrequencyValue(),
                link.getShot().getFrequencyUnit()
            );
            if (date == null) continue;
            int days = (int) ChronoUnit.DAYS.between(today, date);
            for (int targetDays : reminderDays) {
                if (!shouldSend(days, targetDays, catchUp)) continue;
                if (alreadySent("SHOT", link.getShot().getId(), link.getHorse().getId(), targetDays)) continue;
                String email = link.getHorse().getOwner() != null ? link.getHorse().getOwner().getEmail() : null;
                if (email == null || email.isBlank()) continue;
                String subject = shotSubject;
                String body = render(shotBody, link.getHorse().getHorseName(), date, targetDays);
                mailService.sendToRecipients(List.of(email.trim()), subject, body);
                saveLog("SHOT", link.getShot().getId(), link.getHorse().getId(), targetDays);
            }
        }
    }

    private void notifyTreatments(LocalDate today, boolean catchUp) {
        List<HorseTreatment> links = horseTreatmentRepository.findAll();
        for (HorseTreatment link : links) {
            if (link.getTreatment() == null || link.getHorse() == null) continue;
            LocalDate date = resolveNextDate(
                link.getTreatment().getDate(),
                link.getTreatment().getFrequencyValue(),
                link.getTreatment().getFrequencyUnit()
            );
            if (date == null) continue;
            int days = (int) ChronoUnit.DAYS.between(today, date);
            for (int targetDays : reminderDays) {
                if (!shouldSend(days, targetDays, catchUp)) continue;
                if (alreadySent("TREATMENT", link.getTreatment().getId(), link.getHorse().getId(), targetDays)) continue;
                String email = link.getHorse().getOwner() != null ? link.getHorse().getOwner().getEmail() : null;
                if (email == null || email.isBlank()) continue;
                String subject = treatmentSubject;
                String body = render(treatmentBody, link.getHorse().getHorseName(), date, targetDays);
                mailService.sendToRecipients(List.of(email.trim()), subject, body);
                saveLog("TREATMENT", link.getTreatment().getId(), link.getHorse().getId(), targetDays);
            }
        }
    }

    private boolean alreadySent(String type, Long entityId, Long horseId, int days) {
        return notificationLogRepository.existsByEventKey(type, entityId, horseId, days);
    }

    private void saveLog(String type, Long entityId, Long horseId, int days) {
        NotificationLog log = new NotificationLog();
        log.setEventType(type);
        log.setEntityId(entityId);
        log.setHorseId(horseId);
        log.setDaysBefore(days);
        log.setSentAt(LocalDateTime.now());
        notificationLogRepository.save(log);
    }

    private static String render(String template, String horseName, LocalDate date, int days) {
        return template
            .replace("{horse}", horseName != null ? horseName : "-")
            .replace("{date}", date != null ? date.toString() : "-")
            .replace("{days}", String.valueOf(days));
    }

    private static boolean shouldSend(int days, int targetDays, boolean catchUp) {
        if (days < 0) return false;
        if (catchUp) {
            return days <= targetDays;
        }
        return days == targetDays;
    }

    private static LocalDate resolveNextDate(LocalDate baseDate, Integer frequencyValue, String frequencyUnit) {
        if (baseDate == null) return null;
        if (frequencyValue == null || frequencyUnit == null || frequencyUnit.isBlank()) {
            return baseDate;
        }
        try {
            ChronoUnit unit = ChronoUnit.valueOf(frequencyUnit.toUpperCase());
            return baseDate.plus(frequencyValue, unit);
        } catch (Exception e) {
            return baseDate;
        }
    }

    private static List<Integer> parseDays(String raw) {
        List<Integer> days = new ArrayList<>();
        if (raw == null || raw.isBlank()) {
            days.add(7);
            days.add(2);
            return days;
        }
        for (String part : raw.split(",")) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;
            try {
                days.add(Integer.parseInt(trimmed));
            } catch (NumberFormatException ignored) {
            }
        }
        if (days.isEmpty()) {
            days.add(7);
            days.add(2);
        }
        return days;
    }
}
