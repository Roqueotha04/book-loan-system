package com.library.loansystem.Entities;

import com.library.loansystem.Entities.Enums.BookCopyState;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    Book book;

    private BookCopyState state;

    @OneToMany (mappedBy = "bookCopy", cascade = CascadeType.PERSIST)
    private List<Loan> loanList = new ArrayList<>();
}
