package com.library.loansystem.Controllers;

import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.DTO.Response.UserEntityResponse;
import com.library.loansystem.Services.UserEntityService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserEntityController {

    private final UserEntityService userEntityService;

    public UserEntityController(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    @Operation(
            summary = "Get all users",
            description = "Returns a list of all users in the system."
    )
    @GetMapping
    public List<UserEntityResponse> findAll() {
        return userEntityService.findAll();
    }

    @Operation(
            summary = "Find user by ID",
            description = "Returns the user with the specified ID, if it exists."
    )
    @GetMapping("/{id}")
    public UserEntityResponse findById(@PathVariable Long id) {
        return userEntityService.findById(id);
    }

    @Operation(
            summary = "Search users by username",
            description = "Returns a list of users whose username contains the given value (case-insensitive)."
    )
    @GetMapping("/search")
    public List<UserEntityResponse> searchByUsername(@RequestParam String username) {
        return userEntityService.searchByUsername(username);
    }

    @Operation(
            summary = "Find user by email",
            description = "Returns the user with the specified email, if it exists."
    )
    @GetMapping("/by-email")
    public UserEntityResponse findByEmail(@RequestParam String email) {
        return userEntityService.findByEmail(email);
    }

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user if email and username are not already in use."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserEntityResponse save(@Valid @RequestBody UserEntityRequest userEntityRequest) {
        return userEntityService.save(userEntityRequest);
    }

    @Operation(
            summary = "Update a user",
            description = "Updates the data of the user with the specified ID."
    )
    @PutMapping("/{id}")
    public UserEntityResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UserEntityRequest userEntityRequest
    ) {
        return userEntityService.update(id, userEntityRequest);
    }

    @Operation(
            summary = "Delete user permanently",
            description = "Deletes a user permanently if they have no active loans."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermanently(@PathVariable Long id) {
        userEntityService.deletePermanently(id);
    }

    @Operation(
            summary = "Deactivate user",
            description = "Deactivates a user if they have no active loans."
    )
    @PatchMapping("/{id}/deactivate")
    public UserEntityResponse deactivate(@PathVariable Long id) {
        return userEntityService.deactivate(id);
    }

    @Operation(
            summary = "Activate user",
            description = "Activates a previously deactivated user."
    )
    @PatchMapping("/{id}/activate")
    public UserEntityResponse activate(@PathVariable Long id) {
        return userEntityService.activate(id);
    }
}