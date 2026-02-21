package com.library.loansystem.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Loan {

    public Loan (UserEntity userEntity, BookCopy bookCopy, LocalDate dueDate){
        this.bookCopy=bookCopy;
        this.userEntity = userEntity;
        this.startDate= LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        this.dueDate = dueDate;
        this.endDate = null;
    }

    public Loan (){
        this.startDate= LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;

    private LocalDate dueDate;

    private LocalDate endDate;

    @ManyToOne
    private UserEntity userEntity;

    @ManyToOne
    private BookCopy bookCopy;
}
