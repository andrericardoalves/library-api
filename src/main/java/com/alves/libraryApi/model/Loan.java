package com.alves.libraryApi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Loan {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private LocalDate loanDate;
    @Column
    private Boolean returned;
    @OneToOne
    private Customer customer;
    @OneToMany(mappedBy = "loan")
    private List<Book> listBook;
}

