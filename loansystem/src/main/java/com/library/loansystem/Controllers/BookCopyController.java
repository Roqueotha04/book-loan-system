package com.library.loansystem.Controllers;

import com.library.loansystem.DTO.Request.BookCopyRequest;
import com.library.loansystem.DTO.Request.BookCopyStateRequest;
import com.library.loansystem.DTO.Response.BookCopyResponse;
import com.library.loansystem.Entities.Enums.BookCopyState;
import com.library.loansystem.Services.BookCopyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book-copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @Operation(
            summary = "Get all book copies by book",
            description = "Returns all copies of a specific book. Can be filtered by status (available, loaned, damaged, lost)."
    )
    @GetMapping("/book/{isbn}")
    public List<BookCopyResponse> findAllByBook(@PathVariable String isbn,
                                                @RequestParam (required = false) BookCopyState state) {
        return bookCopyService.findAllByBook(isbn, state);
    }

    @Operation(
            summary = "Get a book copy by ID",
            description = "Returns a single book copy details based on its unique ID."
    )
    @GetMapping("/{id}")
    public BookCopyResponse findById(@PathVariable Long id) {
        return bookCopyService.findById(id);
    }

    @Operation(
            summary = "Create a new book copy",
            description = "Adds a new copy for a specific book."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookCopyResponse save(@Valid @RequestBody BookCopyRequest bookCopyRequest) {
        return bookCopyService.save(bookCopyRequest);
    }

    @Operation(
            summary = "Change book copy state",
            description = "Updates the state of a book copy."
    )
    @PatchMapping("/{id}/state")
    public BookCopyResponse patchState(
            @PathVariable Long id,
            @Valid @RequestBody BookCopyStateRequest bookCopyStateRequest
    ) {
        return bookCopyService.patchState(id, bookCopyStateRequest.state());
    }

    @Operation(
            summary = "Delete a book copy",
            description = "Deletes a book copy if it is not loaned."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookCopyService.delete(id);
    }

    @Operation(
            summary = "Count book copies by ISBN",
            description = "Returns the total number of available copies associated with a specific book ISBN."
    )
    @GetMapping("/available/{isbn}")
    public int countAvailableByBookIsbn(@PathVariable String isbn) {
        return bookCopyService.countAvailableByBookIsbn(isbn);
    }

}
