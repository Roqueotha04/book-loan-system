package com.library.loansystem.Controllers;

import com.library.loansystem.DTO.Request.LoanRequest;
import com.library.loansystem.DTO.Response.LoanResponse;
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
            summary = "Get all loans",
            description = "Returns a list of all loans in the system."
    )
    @GetMapping
    public List<LoanResponse> findAll() {
        return loanService.findAll();
    }

    @Operation(
            summary = "Get active loans",
            description = "Returns all active (not returned) loans."
    )
    @GetMapping("/active")
    public List<LoanResponse> findActiveLoans() {
        return loanService.findActiveLoans();
    }

    @Operation(
            summary = "Get returned loans",
            description = "Returns all returned loans."
    )
    @GetMapping("/returned")
    public List<LoanResponse> findReturnedLoans() {
        return loanService.findReturnedLoans();
    }

    @Operation(
            summary = "Get overdue loans",
            description = "Returns all active loans that are overdue."
    )
    @GetMapping("/overdue")
    public List<LoanResponse> findOverdueLoans() {
        return loanService.findOverdueLoans();
    }

    @Operation(
            summary = "Get loans by user",
            description = "Returns all active loans for a specific user."
    )
    @GetMapping("/user/{userId}")
    public List<LoanResponse> findByUser(@PathVariable Long userId) {
        return loanService.findByUser(userId);
    }

    @Operation(
            summary = "Get loans by book",
            description = "Returns all active loans for a specific book."
    )
    @GetMapping("/book/{bookId}")
    public List<LoanResponse> findByBook(@PathVariable Long bookId) {
        return loanService.findByBook(bookId);
    }

    @Operation(
            summary = "Find loan by ID",
            description = "Returns the loan with the specified ID, if it exists."
    )
    @GetMapping("/{id}")
    public LoanResponse findById(@PathVariable Long id) {
        return loanService.findById(id);
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

}
