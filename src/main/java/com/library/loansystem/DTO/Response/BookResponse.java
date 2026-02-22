package com.library.loansystem.DTO.Response;

import com.library.loansystem.Entities.Enums.BookGenre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public record BookResponse(Long id,
                            String name,
                            BookGenre genre,
                            Boolean active,
                            String isbn,
                            PublisherResponse publisher,
                            List<AuthorResponse> authors) {

}
