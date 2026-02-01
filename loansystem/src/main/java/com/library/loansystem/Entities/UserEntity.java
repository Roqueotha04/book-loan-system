package com.library.loansystem.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class UserEntity {

    public UserEntity(){
        this.active=true;
    }

    public UserEntity(String email, String username, String password){
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

    @Column(name = "is_enabled")
    private boolean isEnabled = true;
    @Column(name = "account_no_expired")
    private boolean accountNoExpired = true;
    @Column(name = "account_no_locked")
    private boolean accountNoLocked = true;
    @Column(name = "credentials_no_expired")
    private boolean credentialsNoExpired = true;

    @OneToMany (mappedBy = "userEntity", cascade = CascadeType.PERSIST)
    private List<Loan>loanList = new ArrayList<>();

    @ManyToMany(fetch =  FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn (name = "user_id"),
            inverseJoinColumns = @JoinColumn (name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
