package com.alves.libraryApi.service;

import com.alves.libraryApi.builder.SimpleMailMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class EmailService {
    @Value("${application.mail.default-remitter}")
    private String remitter;

    @Autowired
    private JavaMailSender javaMailSender;

    public boolean sendMails(String message, List<String> mailsList) throws  MailException {
        boolean result = false;
        String[] mails = mailsList.toArray(new String[mailsList.size()]);

        SimpleMailMessage mailMessage =
                SimpleMailMessageBuilder.builder()
                        .from(remitter)
                        .subject("The book is with overdue for Loan")
                        .text(message)
                        .to(mails)
                        .build();
        try {
            javaMailSender.send(mailMessage);
          return  result = true;
        }catch (Exception ex){
            return result;
        }

    }
}
