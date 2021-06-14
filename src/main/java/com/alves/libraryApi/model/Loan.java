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
    private Long id;
    @Column
    private LocalDate loanDate;
    @Column
    private Boolean returned;
    @OneToOne
    private Customer customer;

    @OneToMany(cascade = { CascadeType.PERSIST })
    private List<Book> books;
}

