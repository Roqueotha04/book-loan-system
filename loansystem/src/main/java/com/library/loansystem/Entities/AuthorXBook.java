package com.library.loansystem.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "author_x_book")
public class AuthorXBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

}
