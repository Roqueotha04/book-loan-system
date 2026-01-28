package com.library.loansystem.Controllers;

import com.library.loansystem.DTO.Request.LoanRequest;
import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.Entities.Enums.LoanStatus;
import com.library.loansystem.Services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {
//
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Operation(
            summary = "Create a new loan",
            description = "Creates a new loan for a user and a book."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse createLoan(@Valid @RequestBody LoanRequest loanRequest) {
        return loanService.createLoan(loanRequest);
    }

    @Operation(
            summary = "Return a loan",
            description = "Marks a loan as returned and makes the book copy available again."
    )
    @PatchMapping("/{id}/return")
    public LoanResponse returnLoan(@PathVariable Long id) {
        return loanService.returnLoan(id);
    }

    @Operation(
            summary = "Get loans with filters",
            description = "Returns a list of loans. Can be filtered by status (active, returned, overdue)."
    )
    @GetMapping
    public List<LoanResponse> findAll(@RequestParam(required = false)LoanStatus status) {
        return loanService.findAll(status);
    }

    @Operation(
            summary = "Get loans by user",
            description = "Returns all active loans for a specific user."
    )
    @GetMapping("/user/{userId}")
    public List <LoanResponse> findByUser(@PathVariable ("userId") Long userId,
                                          @RequestParam(required = false) LoanStatus status){
        return loanService.findByUser(userId, status);
    }

    @Operation(
            summary = "Get loans by book",
            description = "Returns all active loans for a specific book."
    )
    @GetMapping("/book/{isbn}")
    public List<LoanResponse> findByBookIsbn(@PathVariable ("isbn") String isbn,
                                             @RequestParam (required = false) LoanStatus status)
    {
        return loanService.findByBook(isbn, status);
    }

    @Operation(
            summary = "Find loan by ID",
            description = "Returns the loan with the specified ID, if it exists."
    )
    @GetMapping("/{id}")
    public LoanResponse findById(@PathVariable Long id) {
        return loanService.findById(id);
    }



}
