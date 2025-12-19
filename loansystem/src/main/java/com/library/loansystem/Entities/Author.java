package com.library.loansystem.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@Entity
public class Author {

    public Author (String name, String lastName){
        this.name= name;
        this.lastName=lastName;
    }

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String lastName;

    @OneToMany (mappedBy = "author", cascade = CascadeType.PERSIST)
    private List<AuthorXBook> authorXBooks=new ArrayList<>();
}
