package com.library.loansystem.Controllers;

import com.library.loansystem.DTO.Author.AuthorRequest;
import com.library.loansystem.DTO.Author.AuthorResponse;
import com.library.loansystem.Services.AuthorService;
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

    @GetMapping
    public List<AuthorResponse> getAll() {
        return authorService.findAll();
    }

    @GetMapping("/{id}")
    public AuthorResponse getById(@PathVariable Long id) {
        return authorService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse create(@RequestBody AuthorRequest author) {
        return authorService.save(author);
    }

    @PutMapping("/{id}")
    public AuthorResponse update(
            @PathVariable Long id,
            @RequestBody AuthorRequest author
    ) {
        return authorService.update(id, author);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        authorService.deleteById(id);
    }
}
