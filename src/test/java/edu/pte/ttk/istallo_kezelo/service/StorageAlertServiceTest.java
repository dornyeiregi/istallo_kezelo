package edu.pte.ttk.istallo_kezelo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.repository.NotificationLogRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for StorageAlertService behavior.
 */
@ExtendWith(MockitoExtension.class)
class StorageAlertServiceTest {

    @Mock
    private MailService mailService;

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Test
    void notifyLowStock_sendsMailAndCreatesLog() {
        StorageAlertService storageAlertService = new StorageAlertService(
            mailService,
            "Low stock {threshold}",
            "Items:\n{lines}",
            "7",
            notificationLogRepository
        );

        Item item = ServiceTestSupport.item(1L, "Hay");
        Storage storage = ServiceTestSupport.storage(2L, item, 10.0, 2.0);

        when(mailService.isEnabled()).thenReturn(true);
        when(mailService.getAdminEmails()).thenReturn(List.of("admin@example.com"));
        when(notificationLogRepository.existsByEventKey(anyString(), anyLong(), any(), anyInt())).thenReturn(false);

        storageAlertService.notifyLowStock(List.of(storage));

        verify(mailService).sendToRecipients(any(), anyString(), anyString());
        verify(notificationLogRepository).save(any());
    }
}
