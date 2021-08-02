package com.alves.libraryApi.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Loan {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;
    @Column
    private LocalDate loanDate;
    @Column
    private Boolean returned;
    @OneToOne(cascade = CascadeType.ALL)
    private Customer customer;

    @OneToMany()
    private List<Book> books;
}

