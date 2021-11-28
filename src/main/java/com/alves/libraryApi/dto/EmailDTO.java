package com.alves.libraryApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {

    private String subject;
    @Size(max = 4000)
    private String text;
    @Email
    private List<String> to;
    private String cc;
    private LocalDateTime sentDate;
}
