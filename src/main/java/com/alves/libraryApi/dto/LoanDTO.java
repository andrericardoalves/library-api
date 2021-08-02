package com.alves.libraryApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private Long id;
    @NotEmpty
    private Long idCustomer;
    @NotEmpty
    private List<BookDTO> booksDTO ;
}


