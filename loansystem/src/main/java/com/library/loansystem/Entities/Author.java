package com.library.loansystem.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Author {

    public Author (String name, String lastName, String nationality){
        this.name= name;
        this.lastName=lastName;
        this.nationality=nationality;
    }

    public Author (Long id, String name, String lastName, String nationality){
        this.id =id;
        this.name= name;
        this.lastName=lastName;
        this.nationality=nationality;
    }

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String lastName;

    private String nationality;

    @OneToMany (mappedBy = "author", cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
    private List<AuthorXBook> authorXBooks=new ArrayList<>();
}
