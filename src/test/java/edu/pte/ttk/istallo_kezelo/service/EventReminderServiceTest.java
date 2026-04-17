package edu.pte.ttk.istallo_kezelo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.HorseFarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseTreatmentRepository;
import edu.pte.ttk.istallo_kezelo.repository.NotificationLogRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for EventReminderService behavior.
 */
@ExtendWith(MockitoExtension.class)
class EventReminderServiceTest {

    @Mock
    private HorseFarrierAppRepository horseFarrierAppRepository;

    @Mock
    private HorseShotRepository horseShotRepository;

    @Mock
    private HorseTreatmentRepository horseTreatmentRepository;

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private MailService mailService;

    @Test
    void sendRemindersNow_sendsShotReminderAndLogs() {
        EventReminderService eventReminderService = new EventReminderService(
            horseFarrierAppRepository,
            horseShotRepository,
            horseTreatmentRepository,
            notificationLogRepository,
            mailService,
            "2",
            "Farrier subject",
            "{horse} {date} {days}",
            "Shot subject",
            "{horse} {date} {days}",
            "Treatment subject",
            "{horse} {date} {days}"
        );

        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        Shot shot = ServiceTestSupport.shot(4L, "Tetanosz");
        shot.setDate(LocalDate.now().plusDays(2));
        shot.setFrequencyUnit(null);
        shot.setFrequencyValue(null);
        HorseShot link = ServiceTestSupport.horseShot(5L, horse, shot);

        when(mailService.isEnabled()).thenReturn(true);
        when(horseFarrierAppRepository.findAll()).thenReturn(List.of());
        when(horseShotRepository.findAll()).thenReturn(List.of(link));
        when(horseTreatmentRepository.findAll()).thenReturn(List.of());
        when(notificationLogRepository.existsByEventKey(anyString(), anyLong(), anyLong(), anyInt()))
            .thenReturn(false);

        eventReminderService.sendRemindersNow();

        verify(mailService).sendToRecipients(any(), anyString(), anyString());
        verify(notificationLogRepository).save(any());
    }
}
