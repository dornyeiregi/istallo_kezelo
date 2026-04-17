package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import edu.pte.ttk.istallo_kezelo.service.StorageAlertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

/**
 * Test class for MailController behavior.
 */
@ExtendWith(MockitoExtension.class)
class MailControllerTest {

    @Mock
    private StorageAlertService storageAlertService;

    @InjectMocks
    private MailController mailController;

    @Test
    void sendTest_returnsOk() {
        var response = mailController.sendTest("a@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Teszt e-mail elküldve.", response.getBody());
        verify(storageAlertService).sendTestMail("a@example.com");
    }
}
