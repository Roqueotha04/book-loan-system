package com.library.loansystem.Controllers;

import com.library.loansystem.DTO.Request.AuthorRequest;
import com.library.loansystem.DTO.Response.AuthorResponse;
import com.library.loansystem.Services.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(
            summary = "Get all authors",
            description = "Returns a list of all authors in the system."
    )
    @GetMapping
    public List<AuthorResponse> getAll() {
        return authorService.findAll();
    }

    @Operation(
            summary = "Find an author by ID",
            description = "Returns the author with the specified ID, if it exists."
    )
    @GetMapping("/{id}")
    public AuthorResponse getById(@PathVariable Long id) {
        return authorService.findById(id);
    }

    @Operation(
            summary = "Create a new author",
            description = "Adds a new author to the system."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse create(@Valid @RequestBody AuthorRequest author) {
        return authorService.save(author);
    }

    @Operation(
            summary = "Update an author",
            description = "Updates the author with the specified ID using the provided data."
    )
    @PutMapping("/{id}")
    public AuthorResponse update(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequest author
    ) {
        return authorService.update(id, author);
    }

    @Operation(
            summary = "Delete an author",
            description = "Deletes the author with the specified ID from the system."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        authorService.deleteById(id);
    }
}
