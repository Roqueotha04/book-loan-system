package com.library.loansystem.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User {

    public User (String email, String username, String password){
        this.email=email;
        this.username=username;
        this.password=password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String username;

    private String password;

    @OneToMany (mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Loan>loanList = new ArrayList<>();
}
