package com.library.loansystem;

import com.library.loansystem.DTO.Response.AuthorResponse;
import com.library.loansystem.DTO.Response.BookResponse;
import com.library.loansystem.DTO.Response.PublisherResponse;
import com.library.loansystem.Entities.*;
import com.library.loansystem.Entities.Enums.BookCopyState;
import com.library.loansystem.Entities.Enums.BookGenre;

import java.time.LocalDate;
import java.util.List;

public class DataProvider {

    public static List<Author> authorListMock(){
        System.out.println("Authors List");
        return List.of(
                new Author("Mariano", "Sigman", "Argentinian"),
                new Author("Jorge Luis", "Borges", "Argentinian"),
                new Author("Eric", "Hosbawn", "British")
        );
    }

    public static List<Publisher> publisherListMock(){
        return List.of(
                new Publisher("Oxford University Press"),
                new Publisher("Cambridge University Press"),
                new Publisher("Pearson")
        );
    }

    public static List<Book> bookListMock(){
        return List.of(
                new Book("1984", BookGenre.FICTION, "8789876298523",new Publisher(1L, "Publisher 1")),
                new Book("Harry Potter and the Philosopher's Stone", BookGenre.FANTASY, "8789876298524",new Publisher(2L,"Publisher 2")),
                new Book("The Lord of the Rings", BookGenre.FANTASY,"8789876298525", new Publisher(3L,"Publisher 3"))
        );
    }

    public static List<User> userListMock(){
        return List.of(
                new User("lionelmessi10@gmail.com", "lionel", "messi"),
                new User("denzelwashington@gmail.com", "denzel", "washington"),
                new User("cristianocr7@gmail.com", "cristiano", "ronaldo")
        );
    }

    public static List <Loan> loanListMock(){
        return List.of(
                new Loan(userListMock().get(0), bookCopyListMock().get(0), LocalDate.now().plusDays(3)),
                new Loan(userListMock().get(1), bookCopyListMock().get(1), LocalDate.now().plusDays(10)),
                new Loan(userListMock().get(2), bookCopyListMock().get(2), LocalDate.now().plusDays(15))
        );
    }

    public static List <BookCopy> bookCopyListMock(){
        return List.of(
               new BookCopy(bookListMock().get(0), BookCopyState.AVAILABLE),
               new BookCopy(bookListMock().get(1), BookCopyState.AVAILABLE),
               new BookCopy(bookListMock().get(2), BookCopyState.AVAILABLE)
        );
    }
}
