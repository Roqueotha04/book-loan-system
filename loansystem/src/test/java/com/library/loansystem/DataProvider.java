package com.library.loansystem;

import com.library.loansystem.DTO.Response.AuthorResponse;
import com.library.loansystem.DTO.Response.BookResponse;
import com.library.loansystem.DTO.Response.PublisherResponse;
import com.library.loansystem.Entities.Author;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.Enums.BookGenre;
import com.library.loansystem.Entities.Publisher;

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

    public static List<AuthorResponse> authorResponseListMock() {
        return List.of(
                new AuthorResponse(1L, "Mariano", "Sigman", "Argentinian"),
                new AuthorResponse(2L, "Jorge Luis", "Borges", "Argentinian"),
                new AuthorResponse(3L, "Eric", "Hosbawn", "British")
        );
    }

    public static List<Publisher> publisherListMock(){
        return List.of(
                new Publisher("Oxford University Press"),
                new Publisher("Cambridge University Press"),
                new Publisher("Pearson")
        );
    }

    public static List<PublisherResponse> publisherResponseListMock(){
        return List.of(
                new PublisherResponse(1L,"Oxford University Press"),
                new PublisherResponse(2L, "Cambridge University Press"),
                new PublisherResponse(3L, "Pearson")
        );
    }

    public static List<Book> bookListMock(){
        return List.of(
                new Book("1984", 15, BookGenre.FICTION, new Publisher(1L, "Publisher 1")),
                new Book("Harry Potter and the Philosopher's Stone", 20, BookGenre.FANTASY, new Publisher(2L,"Publisher 2")),
                new Book("The Lord of the Rings", 12, BookGenre.FANTASY, new Publisher(3L,"Publisher 3"))
        );
    }

    public static List<BookResponse> bookResponseListMock(){
        return List.of(
                new BookResponse(1L, "1984", BookGenre.FICTION, 15,true, new PublisherResponse(1L, "Publisher 1"), null),
                new BookResponse(2L,"Harry Potter and the Philosopher's Stone", BookGenre.FANTASY, 20, true, new PublisherResponse(2L, "Publisher 2"), null),
                new BookResponse(3L,"The Lord of the Rings", BookGenre.FANTASY, 12, true, new PublisherResponse(3L, "Publisher 3"),null)
        );
    }
}
