package com.library.loansystem.Controllers;

import com.library.loansystem.DTO.Request.BookRequest;
import com.library.loansystem.DTO.Request.UpdateStockRequest;
import com.library.loansystem.DTO.Response.BookResponse;
import com.library.loansystem.Services.BookService;
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

    @GetMapping
    public List<BookResponse> findAll(){
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookResponse findById(@PathVariable Long id){
        return bookService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse save (@RequestBody BookRequest bookRequest){
        return bookService.save(bookRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id){
        bookService.delete(id);
    }

    @PutMapping("/{id}")
    public BookResponse update (@PathVariable Long id, @RequestBody BookRequest bookRequest){
        return bookService.update(id,bookRequest);
    }

    @PatchMapping("/{id}/status")
    public BookResponse changeStatus (@PathVariable Long id){
        return bookService.changeStatus(id);
    }

    @PatchMapping ("/{id}/stock")
    public BookResponse updateStock (@PathVariable Long id, @Valid @RequestBody UpdateStockRequest newStock){
        return bookService.updateStock(id,newStock.stock());
    }

}
