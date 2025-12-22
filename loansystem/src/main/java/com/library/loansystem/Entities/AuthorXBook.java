package com.library.loansystem.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "author_x_book")
public class AuthorXBook {

    public AuthorXBook(Book book, Author author) {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable=false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;


}
