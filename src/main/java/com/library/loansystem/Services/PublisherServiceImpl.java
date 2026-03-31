package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.PublisherRequest;
import com.library.loansystem.DTO.Response.PublisherResponse;
import com.library.loansystem.Entities.Publisher;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Repositories.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
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
        Publisher publisher = getPublisherOrThrow(id);
        return toResponse(publisher);
    }

    @Override
    public List<PublisherResponse> findByName(String name) {
        return publisherRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PublisherResponse save(PublisherRequest request) {
        if (publisherRepository.existsByName(request.name())) {
            throw new BusinessException("Publisher already exists");
        }
        Publisher publisher = new Publisher(request.name());
        return toResponse(publisherRepository.save(publisher));
    }

    @Override
    @Transactional
    public PublisherResponse update(Long id, PublisherRequest request) {
        Publisher publisher = getPublisherOrThrow(id);
        if (publisherRepository.existsByName(request.name())) {
            throw new BusinessException("Publisher already exists");
        }
        publisher.setName(request.name());
        return toResponse(publisherRepository.save(publisher));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Publisher publisher = getPublisherOrThrow(id);
        publisherRepository.delete(publisher);
    }

    public Publisher getPublisherOrThrow(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found" + id));
    }

    private PublisherResponse toResponse(Publisher publisher) {
        return new PublisherResponse(
                publisher.getId(),
                publisher.getName()
        );
    }
}