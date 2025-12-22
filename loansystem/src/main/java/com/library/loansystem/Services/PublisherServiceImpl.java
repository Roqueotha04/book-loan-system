package com.library.loansystem.Services;

import com.library.loansystem.DTO.PublisherRequest;
import com.library.loansystem.DTO.PublisherResponse;
import com.library.loansystem.Entities.Publisher;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Repositories.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherServiceImpl(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Override
    public List<PublisherResponse> findAll() {
        return publisherRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PublisherResponse findById(Long id) {
        return toResponse(getPublisherOrThrow(id));
    }

    @Override
    public PublisherResponse save(PublisherRequest request) {
        Publisher publisher = new Publisher(request.getName());
        return toResponse(publisherRepository.save(publisher));
    }

    @Override
    public PublisherResponse update(Long id, PublisherRequest request) {
        Publisher publisher = getPublisherOrThrow(id);
        publisher.setName(request.getName());
        return toResponse(publisherRepository.save(publisher));
    }

    @Override
    public void deleteById(Long id) {
        Publisher publisher = getPublisherOrThrow(id);
        publisherRepository.delete(publisher);
    }

    // Private utility methods

    public Publisher getPublisherOrThrow(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Publisher not found with id: " + id));
    }

    private PublisherResponse toResponse(Publisher publisher) {
        return new PublisherResponse(
                publisher.getId(),
                publisher.getName()
        );
    }
}