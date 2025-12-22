package com.library.loansystem.DTO;

import com.library.loansystem.Entities.Enums.BookGenre;
import com.library.loansystem.Entities.Publisher;

import java.util.List;

public class BookResponse {
    private Long id;
    private String name;
    private BookGenre bookGenre;
    private int stock;
    private Publisher publisher;
    private List<AuthorResponse> authors;
}
