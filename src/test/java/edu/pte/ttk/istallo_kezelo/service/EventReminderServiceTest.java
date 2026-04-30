package edu.pte.ttk.istallo_kezelo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
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
        EventReminderService eventReminderService = service("2");

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

    @Test
    void sendRemindersOnStartup_sendsCatchUpRemindersForFarrierAndTreatment() {
        EventReminderService eventReminderService = service("7,2");

        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        FarrierApp farrierApp = ServiceTestSupport.farrierApp(4L, "Bela");
        farrierApp.setAppointmentDate(LocalDate.now().plusDays(1));
        HorseFarrierApp farrierLink = ServiceTestSupport.horseFarrierApp(5L, horse, farrierApp);
        Treatment treatment = ServiceTestSupport.treatment(6L, "Checkup");
        treatment.setDate(LocalDate.now().minusDays(1));
        treatment.setFrequencyValue(2);
        treatment.setFrequencyUnit("DAYS");
        HorseTreatment treatmentLink = ServiceTestSupport.horseTreatment(7L, horse, treatment);

        when(mailService.isEnabled()).thenReturn(true);
        when(horseFarrierAppRepository.findAll()).thenReturn(List.of(farrierLink));
        when(horseShotRepository.findAll()).thenReturn(List.of());
        when(horseTreatmentRepository.findAll()).thenReturn(List.of(treatmentLink));
        when(notificationLogRepository.existsByEventKey(anyString(), anyLong(), anyLong(), anyInt())).thenReturn(false);

        eventReminderService.sendRemindersOnStartup();

        verify(mailService, times(4)).sendToRecipients(any(), anyString(), anyString());
        verify(notificationLogRepository, times(4)).save(any());
    }

    @Test
    void sendReminders_skipsWhenAlreadySentOrMailDisabled() {
        EventReminderService eventReminderService = service("2");

        when(mailService.isEnabled()).thenReturn(false);

        eventReminderService.sendReminders();

        verify(mailService, never()).sendToRecipients(any(), anyString(), anyString());
    }

    private EventReminderService service(String reminderDays) {
        return new EventReminderService(
            horseFarrierAppRepository,
            horseShotRepository,
            horseTreatmentRepository,
            notificationLogRepository,
            mailService,
            reminderDays,
            "Farrier subject",
            "{horse} {date} {days}",
            "Shot subject",
            "{horse} {date} {days}",
            "Treatment subject",
            "{horse} {date} {days}"
        );
    }
}
