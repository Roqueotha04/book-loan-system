package com.library.loansystem.Controllers;

import com.library.loansystem.DTO.Request.UserRequest;
import com.library.loansystem.DTO.Response.UserResponse;
import com.library.loansystem.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get all users",
            description = "Returns a list of all users in the system."
    )
    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @Operation(
            summary = "Find user by ID",
            description = "Returns the user with the specified ID, if it exists."
    )
    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation(
            summary = "Search users by username",
            description = "Returns a list of users whose username contains the given value (case-insensitive)."
    )
    @GetMapping("/search")
    public List<UserResponse> searchByUsername(@RequestParam String username) {
        return userService.searchByUsername(username);
    }

    @Operation(
            summary = "Find user by email",
            description = "Returns the user with the specified email, if it exists."
    )
    @GetMapping("/by-email")
    public UserResponse findByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user if email and username are not already in use."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse save(@Valid @RequestBody UserRequest userRequest) {
        return userService.save(userRequest);
    }

    @Operation(
            summary = "Update a user",
            description = "Updates the data of the user with the specified ID."
    )
    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest userRequest
    ) {
        return userService.update(id, userRequest);
    }

    @Operation(
            summary = "Delete user permanently",
            description = "Deletes a user permanently if they have no active loans."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermanently(@PathVariable Long id) {
        userService.deletePermanently(id);
    }

    @Operation(
            summary = "Deactivate user",
            description = "Deactivates a user if they have no active loans."
    )
    @PatchMapping("/{id}/deactivate")
    public UserResponse deactivate(@PathVariable Long id) {
        return userService.deactivate(id);
    }

    @Operation(
            summary = "Activate user",
            description = "Activates a previously deactivated user."
    )
    @PatchMapping("/{id}/activate")
    public UserResponse activate(@PathVariable Long id) {
        return userService.activate(id);
    }
}