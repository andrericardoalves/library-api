package com.alves.libraryApi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestEntityManager
public class EmailServiceTest {

    @MockBean
    public EmailService emailService;

    @Test
    public void shouldSendEmailsSuccess(){
        String message = "Hi. You have late loan.";
        List<String> mailsList = Arrays.asList("andre@mailtrap.com", "ingrid@mailtrap.com");

        Mockito.when(emailService.sendMails(message, mailsList)).thenReturn(true);
        boolean result = emailService.sendMails(message, mailsList);
        Assertions.assertTrue(result);
        Assertions.assertDoesNotThrow( () -> emailService.sendMails(message, mailsList)) ;
    }

    @Test
    public void shouldReturnExceptionSendEmails() {
        String message = "Hi. You have late loan.";
        List<String> mailsList = Arrays.asList("andre@mailtrap.com", "ingrid@mailtrap.com");

        Mockito.when(emailService.sendMails(Mockito.anyString(), Mockito.anyList()))
                .thenThrow(Mockito.mock(MailException.class));
        Assertions.assertThrows(MailException.class, () -> emailService.sendMails(message, mailsList));

    }
}
