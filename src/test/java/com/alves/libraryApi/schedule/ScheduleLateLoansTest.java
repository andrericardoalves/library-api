package com.alves.libraryApi.schedule;


import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.model.Loan;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestEntityManager
public class ScheduleLateLoansTest {

    @MockBean
    private ScheduleLateLoans scheduleLateLoans;

    @Test
    public void shouldReturnListEmailWithLateLoans(){
        List<String> mailsList = Arrays.asList("andre@mailtrap.com", "ingrid@mailtrap.com");
        Loan loanOne = Loan.builder().customer(Customer.builder().email("andre@mailtrap.com").build()).build();
        Loan loanTwo = Loan.builder().customer(Customer.builder().email("ingrid@mailtrap.com").build()).build();

        Mockito.when(scheduleLateLoans.listEmailsWithLateLoans(Mockito.anyList()))
                .thenReturn(mailsList);
       List<String> listEmails = scheduleLateLoans.listEmailsWithLateLoans(Arrays.asList(loanOne,loanTwo));

        Assertions.assertEquals(2, listEmails.size());
    }

    @Test
    public void shouldCallScheduleAtMidnightNext5DaysInARow(){
        String CRON_EXP = "0 0 0 1/1 * ?";
        CronExpression cronGen =  CronExpression.parse(CRON_EXP);
        LocalDateTime oneDayAtMidnightCron = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
        LocalDateTime oneDayAtMidnight = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));

        for (int i = 1; i < 5; i++) {
            oneDayAtMidnightCron = cronGen.next(oneDayAtMidnightCron);
            oneDayAtMidnight = oneDayAtMidnight.plusDays(1);
            Assertions.assertEquals(oneDayAtMidnightCron , oneDayAtMidnight);
        }
    }

}
