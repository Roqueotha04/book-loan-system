package com.library.loansystem.Entities;

import com.library.loansystem.Entities.Enums.BookGenre;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Book {

    public Book (String name, int stock, BookGenre bookGenre,String isbn, Publisher publisher){
        this.name=name;
        this.stock=stock;
        this.active=true;
        this.genre= bookGenre;
        this.isbn = isbn;
        this.publisher=publisher;
    }

    public Book (){
        this.active=true;
    }

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String isbn;

    @Enumerated(EnumType.STRING)
    private BookGenre genre;

    private int stock;

    private Boolean active;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List <AuthorXBook> authorXBooks= new ArrayList<>();

    @ManyToOne
    private Publisher publisher;

    @OneToMany
    private List<BookCopy> bookCopyList = new ArrayList<>();

}
