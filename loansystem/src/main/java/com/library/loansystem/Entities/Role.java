package com.library.loansystem.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {

    public Role (String role){
        this.role=role;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// Admin, Librarian, User
    @Column(name = "role_name", nullable = false, unique = true)
    private String role;
}
