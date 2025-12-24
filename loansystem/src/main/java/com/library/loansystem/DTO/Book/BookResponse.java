package com.library.loansystem.DTO.Book;

import com.library.loansystem.DTO.Author.AuthorResponse;
import com.library.loansystem.DTO.PublisherResponse;
import com.library.loansystem.Entities.Enums.BookGenre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String name;
    private BookGenre genre;
    private int stock;
    private Boolean active;
    private PublisherResponse publisher;
    private List<AuthorResponse> authors;
}
