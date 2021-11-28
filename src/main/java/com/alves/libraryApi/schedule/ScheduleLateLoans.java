package com.alves.libraryApi.schedule;

import com.alves.libraryApi.dto.EmailDTO;
import com.alves.libraryApi.model.Loan;
import com.alves.libraryApi.service.EmailService;
import com.alves.libraryApi.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScheduleLateLoans {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    @Value("${application.mail.lateLoans.message.subject}")
    private String subject;

    @Value("${application.mail.lateLoans.message}")
    private String message;

    @Autowired
    private LoanService loanService;
    @Autowired
    private EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans(){
        List<Loan> allLateLoans = loanService.getAllLateLoans();
        List<String> mailsList = listEmailsWithLateLoans(allLateLoans);
        EmailDTO email = EmailDTO.builder()
                        .to(mailsList)
                        .subject(subject)
                        .text(message)
                        .build();
        emailService.sendMails(email);
    }

    public List<String> listEmailsWithLateLoans( List<Loan> allLateLoans){
        List<String> mailsList = allLateLoans.stream()
                .map(loan -> loan.getCustomer().getEmail())
                .collect(Collectors.toList());
        return mailsList;
    }
}
