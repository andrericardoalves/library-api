package com.alves.libraryApi.resource;

import com.alves.libraryApi.dto.EmailDTO;
import com.alves.libraryApi.service.EmailService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@Api("API Book ")
public class EmailResource {

    @Autowired
    private EmailService service;

    @GetMapping
    public boolean sendMails(EmailDTO emailDTO){
        return service.sendMails(emailDTO);
    }
}
