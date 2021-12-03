package com.alves.libraryApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {

    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String email;
    private String surName;

}
