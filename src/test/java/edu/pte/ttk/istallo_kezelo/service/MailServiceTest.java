package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private ObjectProvider<JavaMailSender> mailSenderProvider;

    @Mock
    private UserRepository userRepository;

    @Test
    void getAdminEmails_usesFallbackWhenNoAdmins() {
        when(userRepository.findByUserType(UserType.ADMIN)).thenReturn(List.of());
        MailService mailService = new MailService(mailSenderProvider, userRepository, false, "", "a@example.com, b@example.com");

        List<String> recipients = mailService.getAdminEmails();

        assertEquals(List.of("a@example.com", "b@example.com"), recipients);
    }

    @Test
    void sendToRecipients_whenDisabled_doesNothing() {
        MailService mailService = new MailService(mailSenderProvider, userRepository, false, "", "");

        mailService.sendToRecipients(List.of("a@example.com"), "Subject", "Body");

        verifyNoInteractions(mailSenderProvider);
    }

    @Test
    void getAdminEmails_returnsTrimmedAdminEmailsBeforeFallback() {
        User admin = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        admin.setEmail(" admin@example.com ");
        when(userRepository.findByUserType(UserType.ADMIN)).thenReturn(List.of(admin));
        MailService mailService = new MailService(mailSenderProvider, userRepository, true, "", "fallback@example.com");

        List<String> recipients = mailService.getAdminEmails();

        assertEquals(List.of("admin@example.com"), recipients);
    }

    @Test
    void sendToRecipients_sendsMailWhenEnabledAndSenderAvailable() {
        JavaMailSender mailSender = org.mockito.Mockito.mock(JavaMailSender.class);
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSenderProvider.getIfAvailable()).thenReturn(mailSender);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        MailService mailService = new MailService(mailSenderProvider, userRepository, true, "stable@example.com", "");

        mailService.sendToRecipients(List.of("a@example.com"), "Subject", "Body");

        verify(mailSender).send(any(MimeMessage.class));
    }
}
