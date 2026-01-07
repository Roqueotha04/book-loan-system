package com.library.loansystem.Controllers;

import com.library.loansystem.DTO.Request.PublisherRequest;
import com.library.loansystem.DTO.Response.PublisherResponse;
import com.library.loansystem.Services.PublisherService;
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

        @GetMapping
        public List<PublisherResponse> findAll() {
            return publisherService.findAll();
        }

        @GetMapping("/{id}")
        public PublisherResponse findById(@PathVariable Long id) {
            return publisherService.findById(id);
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public PublisherResponse save(@RequestBody PublisherRequest request) {
            return publisherService.save(request);
        }

        @PutMapping("/{id}")
        public PublisherResponse update(
                @PathVariable Long id,
                @RequestBody PublisherRequest request
        ) {
            return publisherService.update(id, request);
        }

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void delete(@PathVariable Long id) {
            publisherService.deleteById(id);
        }
    }
