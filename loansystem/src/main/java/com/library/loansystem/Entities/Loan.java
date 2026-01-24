package com.library.loansystem.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Loan {

    public Loan (User user, BookCopy bookCopy, LocalDate dueDate){
        this.bookCopy=bookCopy;
        this.user=user;
        this.active=true;
        this.startDate= LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        this.dueDate = dueDate;
        this.endDate = null;
    }

    public Loan (){
        this.active=true;
        this.startDate= LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean active;

    private LocalDate startDate;

    private LocalDate dueDate;

    private LocalDate endDate;

    @ManyToOne
    private User user;

    @ManyToOne
    private BookCopy bookCopy;
}
