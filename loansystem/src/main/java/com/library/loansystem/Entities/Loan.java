package com.library.loansystem.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Data
@Entity
public class Loan {

    public Loan (User user, Book book, LocalDate endDate){
        this.book=book;
        this.user=user;
        this.active=true;
        this.startDate= LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        this.endDate = endDate;
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

    private LocalDate endDate;

    @ManyToOne
    private User user;

    @ManyToOne
    private Book book;
}
