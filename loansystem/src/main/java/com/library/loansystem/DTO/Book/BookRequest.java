package com.library.loansystem.DTO.Book;

import com.library.loansystem.Entities.Enums.BookGenre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    private String name;
    private BookGenre genre;
    private int stock;
    private Long publisherID;
    private List<Long> authorsIds = new ArrayList<>();

}
