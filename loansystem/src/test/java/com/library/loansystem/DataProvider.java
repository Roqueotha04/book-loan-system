package com.library.loansystem;

import com.library.loansystem.DTO.Response.AuthorResponse;
import com.library.loansystem.DTO.Response.PublisherResponse;
import com.library.loansystem.Entities.Author;
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
}
