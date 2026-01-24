package com.library.loansystem.Entities;

import com.library.loansystem.Entities.Enums.BookCopyState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class BookCopy {

    public BookCopy (Book book, BookCopyState state){
        this.book=book;
        this.state = state;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    Book book;

    private BookCopyState state;

    @OneToMany (mappedBy = "bookCopy", cascade = CascadeType.PERSIST)
    private List<Loan> loanList = new ArrayList<>();
}
