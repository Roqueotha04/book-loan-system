package com.library.loansystem.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class User {

    public User (String gmail, String username, String password){
        this.gmail=gmail;
        this.username=username;
        this.password=password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gmail;

    private String username;

    private String password;

    @OneToMany (mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Loan>loanList = new ArrayList<>();
}
