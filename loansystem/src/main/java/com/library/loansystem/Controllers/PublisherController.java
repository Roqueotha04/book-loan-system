package com.library.loansystem.Controllers;

import com.library.loansystem.DTO.Request.PublisherRequest;
import com.library.loansystem.DTO.Response.PublisherResponse;
import com.library.loansystem.Services.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @Operation(
            summary = "Get all publishers",
            description = "Returns a list of all publishers in the system."
    )
    @GetMapping
    public List<PublisherResponse> findAll() {
        return publisherService.findAll();
    }

    @Operation(
            summary = "Find a publisher by ID",
            description = "Returns the publisher with the specified ID, if it exists."
    )
    @GetMapping("/{id}")
    public PublisherResponse findById(@PathVariable Long id) {
        return publisherService.findById(id);
    }

    @Operation(
            summary = "Create a new publisher",
            description = "Adds a new publisher to the system."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PublisherResponse save(@RequestBody PublisherRequest request) {
        return publisherService.save(request);
    }

    @Operation(
            summary = "Update a publisher",
            description = "Updates the publisher with the specified ID using the provided data."
    )
    @PutMapping("/{id}")
    public PublisherResponse update(
            @PathVariable Long id,
            @RequestBody PublisherRequest request
    ) {
        return publisherService.update(id, request);
    }

    @Operation(
            summary = "Delete a publisher",
            description = "Deletes the publisher with the specified ID from the system."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        publisherService.deleteById(id);
    }
}
