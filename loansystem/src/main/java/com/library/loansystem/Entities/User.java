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
@Entity
public class User {

    public User(){
        this.active=true;
    }

    public User (String email, String username, String password){
        this.email=email;
        this.username=username;
        this.password=password;
        this.active=true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    private Boolean active;

    @OneToMany (mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Loan>loanList = new ArrayList<>();
}
