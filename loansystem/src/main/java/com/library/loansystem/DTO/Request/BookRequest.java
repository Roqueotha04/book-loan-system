package com.library.loansystem.DTO.Request;

import com.library.loansystem.Entities.Enums.BookGenre;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    @Size(max = 50)
    @NotBlank
    private String name;

    @NotNull
    private BookGenre genre;

    @Min(0)
    @Max(1000)
    private int stock;

    @NotBlank
    @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN must be 10 or 13 digits")
    private String isbn;

    @NotNull
    private Long publisherID;

    @NotEmpty
    private List<Long> authorsIds = new ArrayList<>();

}
