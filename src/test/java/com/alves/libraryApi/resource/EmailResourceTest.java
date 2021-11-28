package com.alves.libraryApi.resource;

import com.alves.libraryApi.dto.EmailDTO;
import com.alves.libraryApi.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@WebMvcTest(controllers = EmailResource.class)
@AutoConfigureMockMvc
public class EmailResourceTest {

    static  String EMAIL_API = "/api/email";

    @Autowired
    MockMvc mvc;

    @MockBean
    EmailService emailService;

    @Test
    public void shouldSendEmailWithSuccess() throws Exception {

        EmailDTO email = EmailDTO.builder()
                .to(Arrays.asList("andre@mailtrap.com", "ingrid@mailtrap.com"))
                .subject("This is a subject")
                .text("Hi, This is a message to you")
                .build();
        BDDMockito.given(emailService.sendMails(email)).willReturn(true);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(EMAIL_API)
                .param("message", email.getText())
                .param("emails", email.getTo().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
