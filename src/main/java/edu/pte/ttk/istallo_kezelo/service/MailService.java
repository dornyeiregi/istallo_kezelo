package edu.pte.ttk.istallo_kezelo.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;

/**
 * Infrastructure service for sending email messages to users.
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final UserRepository userRepository;
    private final boolean enabled;
    private final String from;
    private final String fallbackTo;

    public MailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                       UserRepository userRepository,
                       @Value("${app.mail.enabled:false}") boolean enabled,
                       @Value("${app.mail.from:}") String from,
                       @Value("${app.mail.to:}") String fallbackTo) {
        this.mailSenderProvider = mailSenderProvider;
        this.userRepository = userRepository;
        this.enabled = enabled;
        this.from = from;
        this.fallbackTo = fallbackTo;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getAdminEmails() {
        List<String> recipients = new ArrayList<>();
        List<User> admins = userRepository.findByUserType(UserType.ADMIN);
        for (User admin : admins) {
            if (admin.getEmail() != null && !admin.getEmail().isBlank()) {
                recipients.add(admin.getEmail().trim());
            }
        }
        if (recipients.isEmpty() && fallbackTo != null && !fallbackTo.isBlank()) {
            for (String addr : fallbackTo.split(",")) {
                String trimmed = addr.trim();
                if (!trimmed.isEmpty()) recipients.add(trimmed);
            }
        }
        return recipients;
    }

    public void sendToAdmins(String subject, String body) {
        List<String> recipients = getAdminEmails();
        sendToRecipients(recipients, subject, body);
    }

    public void sendToRecipients(List<String> recipients, String subject, String body) {
        if (!enabled) {
            log.debug("Mail disabled (app.mail.enabled=false).");
            return;
        }
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("JavaMailSender not available. Missing spring-boot-starter-mail?");
            return;
        }
        if (recipients == null || recipients.isEmpty()) {
            log.warn("No mail recipients.");
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(recipients.toArray(new String[0]));
            if (from != null && !from.isBlank()) {
                helper.setFrom(from.trim());
            }
            helper.setSubject(subject);
            helper.setText(body, false);
            mailSender.send(message);
            log.info("Mail sent to {} recipient(s).", recipients.size());
        } catch (Exception e) {
            log.error("Failed to send mail: {}", e.getMessage());
        }
    }
}
