package com.library.loansystem.DTO.Response;

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
