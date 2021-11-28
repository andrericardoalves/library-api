package com.alves.libraryApi.service;

import com.alves.libraryApi.builder.SimpleMailMessageBuilder;
import com.alves.libraryApi.dto.EmailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${application.mail.default-remitter}")
    private String remitter;

    @Autowired
    private JavaMailSender javaMailSender;

    public boolean sendMails(EmailDTO emailDTO) throws  MailException {
        boolean result = false;
        String[] mails = emailDTO.getTo().toArray(new String[emailDTO.getTo().size()]);

        SimpleMailMessage mailMessage =
                SimpleMailMessageBuilder.builder()
                        .from(remitter)
                        .subject(emailDTO.getSubject())
                        .text(emailDTO.getText())
                        .to(mails)
                        .build();
        try {
            javaMailSender.send(mailMessage);
          return  result = true;
        }catch (Exception ex){
            return result = false;
        }

    }
}
