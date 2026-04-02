package edu.pte.ttk.istallo_kezelo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.pte.ttk.istallo_kezelo.repository.NotificationLogRepository;
import edu.pte.ttk.istallo_kezelo.model.NotificationLog;
import java.time.LocalDateTime;

/**
 * Application service for low-stock email notifications.
 */
@Service
public class StorageAlertService {

    private static final Logger log = LoggerFactory.getLogger(StorageAlertService.class);

    private final MailService mailService;
    private final String subjectTemplate;
    private final String bodyTemplate;
    private final List<Integer> thresholds;
    private final NotificationLogRepository notificationLogRepository;

    public StorageAlertService(MailService mailService,
                               @Value("${app.mail.low-stock.subject:Alacsony készlet figyelmeztetés}") String subjectTemplate,
                               @Value("${app.mail.low-stock.body:Az alábbi tételek készlete alacsony:\\n\\n{lines}\\nKérlek pótold a készletet.}") String bodyTemplate,
                               @Value("${app.storage.low-days:7}") String lowDays,
                               NotificationLogRepository notificationLogRepository) {
        this.mailService = mailService;
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
        this.thresholds = parseDays(lowDays);
        this.notificationLogRepository = notificationLogRepository;
    }

    public void notifyLowStock(List<Storage> storages) {
        if (!mailService.isEnabled()) {
            log.debug("Mail disabled (app.mail.enabled=false).");
            return;
        }
        List<String> recipients = mailService.getAdminEmails();
        if (recipients.isEmpty()) return;

        for (int threshold : thresholds) {
            List<String> lines = new ArrayList<>();
            for (Storage storage : storages) {
                Double stored = storage.getAmountStored();
                Double inUse = storage.getAmountInUse();
                if (stored == null || inUse == null || inUse <= 0) continue;
                int days = (int) Math.floor(stored / inUse);
                if (days > threshold) continue;
                Long storageId = storage.getId();
                if (storageId == null) continue;
                if (notificationLogRepository.existsByEventKey("STORAGE_LOW", storageId, null, threshold)) {
                    continue;
                }
                String itemName = storage.getItem() != null ? storage.getItem().getName() : "Ismeretlen tétel";
                String line = String.format(Locale.ROOT,
                    "- %s | készlet: %.2f kg | napi használat: %.2f kg | kb. %d nap",
                    itemName, stored, inUse, days);
                lines.add(line);
            }

            if (lines.isEmpty()) {
                continue;
            }

            String subject = subjectTemplate.replace("{threshold}", String.valueOf(threshold));
            String body = bodyTemplate
                .replace("{threshold}", String.valueOf(threshold))
                .replace("{lines}", String.join("\n", lines));
            mailService.sendToRecipients(recipients, subject, body);

            for (Storage storage : storages) {
                Double stored = storage.getAmountStored();
                Double inUse = storage.getAmountInUse();
                if (stored == null || inUse == null || inUse <= 0) continue;
                int days = (int) Math.floor(stored / inUse);
                if (days > threshold) continue;
                Long storageId = storage.getId();
                if (storageId == null) continue;
                if (notificationLogRepository.existsByEventKey("STORAGE_LOW", storageId, null, threshold)) {
                    continue;
                }
                NotificationLog logEntry = new NotificationLog();
                logEntry.setEventType("STORAGE_LOW");
                logEntry.setEntityId(storageId);
                logEntry.setHorseId(null);
                logEntry.setDaysBefore(threshold);
                logEntry.setSentAt(LocalDateTime.now());
                notificationLogRepository.save(logEntry);
            }
        }
    }

    public void sendTestMail(String overrideTo) {
        if (!mailService.isEnabled()) {
            throw new RuntimeException("Mail küldés le van tiltva (app.mail.enabled=false).");
        }
        List<String> recipients = new ArrayList<>();
        if (overrideTo != null && !overrideTo.isBlank()) {
            for (String addr : overrideTo.split(",")) {
                String trimmed = addr.trim();
                if (!trimmed.isEmpty()) recipients.add(trimmed);
            }
        } else {
            recipients = mailService.getAdminEmails();
        }
        if (recipients.isEmpty()) {
            throw new RuntimeException("Nincs címzett (admin email üres és app.mail.to sincs megadva).");
        }
        mailService.sendToRecipients(recipients, "Teszt e-mail", "Ez egy teszt e-mail az istallo_kezelo rendszertől.");
    }

    private static List<Integer> parseDays(String raw) {
        List<Integer> days = new ArrayList<>();
        if (raw == null || raw.isBlank()) {
            days.add(7);
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
        }
        days.sort((a, b) -> b.compareTo(a));
        return days;
    }
}
