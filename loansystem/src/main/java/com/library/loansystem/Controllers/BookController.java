package com.library.loansystem.Controllers;

import com.library.loansystem.DTO.Request.BookRequest;
import com.library.loansystem.DTO.Request.UpdateStockRequest;
import com.library.loansystem.DTO.Response.BookResponse;
import com.library.loansystem.Services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(
            summary = "Get all books",
            description = "Returns a list of all books in the system."
    )
    @GetMapping
    public List<BookResponse> findAll() {
        return bookService.findAll();
    }

    @Operation(
            summary = "Find a book by ID",
            description = "Returns the book with the specified ID, if it exists."
    )
    @GetMapping("/{id}")
    public BookResponse findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @Operation(
            summary = "Create a new book",
            description = "Adds a new book to the system."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse save(@RequestBody BookRequest bookRequest) {
        return bookService.save(bookRequest);
    }

    @Operation(
            summary = "Update a book",
            description = "Updates the book with the specified ID using the provided data."
    )
    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id, @RequestBody BookRequest bookRequest) {
        return bookService.update(id, bookRequest);
    }

    @Operation(
            summary = "Delete a book",
            description = "Deletes the book with the specified ID from the system."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        bookService.delete(id);
    }

    @Operation(
            summary = "Change book status",
            description = "Toggles the status of a book (e.g., available/unavailable)."
    )
    @PatchMapping("/{id}/status")
    public BookResponse changeStatus(@PathVariable Long id) {
        return bookService.changeStatus(id);
    }

    @Operation(
            summary = "Update book stock",
            description = "Updates the stock quantity of a book with the provided value."
    )
    @PatchMapping("/{id}/stock")
    public BookResponse updateStock(@PathVariable Long id, @Valid @RequestBody UpdateStockRequest newStock) {
        return bookService.updateStock(id, newStock.stock());
    }
}



