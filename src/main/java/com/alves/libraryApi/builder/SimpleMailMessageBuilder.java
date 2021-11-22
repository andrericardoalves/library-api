package com.alves.libraryApi.builder;

import org.springframework.mail.SimpleMailMessage;

import java.util.Date;


public class SimpleMailMessageBuilder{

    private SimpleMailMessage simpleMailMessage;



    public SimpleMailMessageBuilder(){}


    public static SimpleMailMessageBuilder builder(){
        SimpleMailMessageBuilder simpleMailMessageBuilder = new SimpleMailMessageBuilder();
        simpleMailMessageBuilder.simpleMailMessage = new SimpleMailMessage();
        return simpleMailMessageBuilder;
    }

    public SimpleMailMessageBuilder from(String from) {
        simpleMailMessage.setFrom(from);
        return this;
    }

    public SimpleMailMessageBuilder replyTo(String replyTo) {
        simpleMailMessage.setReplyTo(replyTo);
        return this;
    }

    public SimpleMailMessageBuilder to(String[] to) {
        simpleMailMessage.setTo(to);
        return this;
    }

    public SimpleMailMessageBuilder cc(String[] cc) {
        simpleMailMessage.setCc(cc);
        return this;
    }

    public SimpleMailMessageBuilder bcc(String[] bcc) {
        simpleMailMessage.setBcc(bcc);
        return this;
    }

    public SimpleMailMessageBuilder sentDate(Date sentDate) {
        simpleMailMessage.setSentDate(sentDate);
        return this;
    }

    public SimpleMailMessageBuilder subject(String subject) {
        simpleMailMessage.setSubject(subject);
        return this;
    }

    public SimpleMailMessageBuilder text(String text) {
        simpleMailMessage.setText(text);
        return this;
    }

    public SimpleMailMessage build(){
        return simpleMailMessage;
    }
}
