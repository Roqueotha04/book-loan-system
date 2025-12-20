package com.library.loansystem.Controllers;

import com.library.loansystem.Entities.Publisher;
import com.library.loansystem.Services.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publishers")
public class PublisherController {

    @Autowired
    PublisherService publisherService;

    @GetMapping()
    public List<Publisher> findAll(){
        return publisherService.findAll();
    }

    @GetMapping("/{id}")
    public Publisher findById (@PathVariable Long id){
        return publisherService.findById(id);
    }

    @PostMapping
    @ResponseStatus (HttpStatus.CREATED)
    public Publisher save (@RequestBody Publisher publisher){
        return publisherService.save(publisher);
    }

    @DeleteMapping("{id}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void delete (@PathVariable  Long id){
        publisherService.deleteById(id);
    }

}
