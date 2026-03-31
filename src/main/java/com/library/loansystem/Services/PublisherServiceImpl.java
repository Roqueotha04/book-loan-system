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
        return toResponse(getPublisherOrThrow(id));
    }

    @Override
    public List<PublisherResponse> findByName(String name) {
        validateNameNotBlank(name);
        return publisherRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PublisherResponse save(PublisherRequest request) {
        validateNameNotBlank(request.name());
        validateNameNotDuplicated(request.name());
        Publisher publisher = new Publisher(request.name().trim());
        return toResponse(publisherRepository.save(publisher));
    }

    @Override
    @Transactional
    public PublisherResponse update(Long id, PublisherRequest request) {
        validateNameNotBlank(request.name());
        validateNameNotDuplicated(request.name());
        Publisher publisher = getPublisherOrThrow(id);
        publisher.setName(request.name().trim());
        return toResponse(publisherRepository.save(publisher));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Publisher publisher = getPublisherOrThrow(id);
        validateNoAssociatedBooks(id);
        publisherRepository.delete(publisher);
    }

    public Publisher getPublisherOrThrow(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with id: " + id));
    }

    private void validateNameNotBlank(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Publisher name cannot be blank");
        }
    }

    private void validateNameNotDuplicated(String name) {
        if (publisherRepository.existsByName(name.trim())) {
            throw new BusinessException("Publisher with name '" + name + "' already exists");
        }
    }

    private void validateNoAssociatedBooks(Long id) {
        if (publisherRepository.existsBookByPublisherId(id)) {
            throw new BusinessException("Cannot delete publisher with associated books");
        }
    }

    private PublisherResponse toResponse(Publisher publisher) {
        return new PublisherResponse(
                publisher.getId(),
                publisher.getName()
        );
    }
}