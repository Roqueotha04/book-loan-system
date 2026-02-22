package com.library.loansystem.DTO.Request;

import com.library.loansystem.Entities.Enums.BookGenre;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public record BookRequest( @Size(max = 50) @NotBlank String name,
                           @NotNull BookGenre genre,
                           @NotBlank @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN must be 10 or 13 digits")String isbn,
                           @NotNull Long publisherID,
                           @NotEmpty List<Long> authorsIds) {
}
