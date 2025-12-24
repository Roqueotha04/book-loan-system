package com.library.loansystem.Entities;

import com.library.loansystem.Entities.Enums.BookGenre;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Book {

    public Book (String name, int stock, BookGenre bookGenre, Publisher publisher){
        this.name=name;
        this.stock=stock;
        this.active=true;
        this.genre= bookGenre;
        this.publisher=publisher;
    }

    public Book (){
        this.active=true;
    }

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private BookGenre genre;

    private int stock;

    private Boolean active;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List <AuthorXBook> authorXBooks= new ArrayList<>();

    @ManyToOne
    private Publisher publisher;

    @OneToMany (mappedBy = "book", cascade = CascadeType.PERSIST)
    private List <Loan> loanList = new ArrayList<>();

}
