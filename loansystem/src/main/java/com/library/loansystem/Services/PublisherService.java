package com.library.loansystem.Services;

import com.library.loansystem.Entities.Publisher;

import java.util.List;

public interface PublisherService {

    public List<Publisher> findAll();
    public Publisher findById(Long id);
    public Publisher save(Publisher publisher);
    public void delete (Long id);
}
