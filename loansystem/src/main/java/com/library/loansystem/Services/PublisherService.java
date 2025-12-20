package com.library.loansystem.Services;

import com.library.loansystem.DTO.PublisherRequest;
import com.library.loansystem.DTO.PublisherResponse;
import com.library.loansystem.Entities.Publisher;

import java.util.List;

public interface PublisherService {

    List<PublisherResponse> findAll();

    PublisherResponse findById(Long id);

    PublisherResponse save(PublisherRequest request);

    PublisherResponse update(Long id, PublisherRequest request);

    void deleteById(Long id);
}
