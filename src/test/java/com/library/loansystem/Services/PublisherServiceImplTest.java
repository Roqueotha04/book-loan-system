package com.library.loansystem.Services;


import com.library.loansystem.DTO.Request.PublisherRequest;
import com.library.loansystem.DTO.Response.PublisherResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Publisher;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Repositories.PublisherRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.crypto.Data;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PublisherServiceImplTest {

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherServiceImpl publisherService;

    @Test
    public void testFindAll(){
        List<Publisher> publisherResponseList = DataProvider.publisherListMock();

        when(publisherRepository.findAll()).thenReturn(publisherResponseList);
        List<PublisherResponse> result=publisherService.findAll();

        assertEquals(publisherResponseList.get(1).getName(), result.get(1).name());
        assertEquals(publisherResponseList.size(), result.size());

        verify(publisherRepository).findAll();
    }

    @Test
    public void testFindById(){
        Publisher publisher = DataProvider.publisherListMock().get(0);

        when(publisherRepository.findById(0L)).thenReturn(Optional.of(publisher));

        PublisherResponse result = publisherService.findById(0L);

        assertNotNull(result);
        assertEquals(publisher.getId(), result.id());
        assertEquals(publisher.getName(), result.name());

        verify(publisherRepository).findById(0L);
    }

    @Test
    public void testFindByName(){
        List<Publisher> publisherList = DataProvider.publisherListMock();

        when(publisherRepository.findByNameContainingIgnoreCase("name")).thenReturn(publisherList);

       List<PublisherResponse> result = publisherService.findByName("name");

        assertNotNull(result);
        assertEquals(publisherList.size(), result.size());
        assertEquals(publisherList.get(1).getName(), result.get(1).name());

        verify(publisherRepository).findByNameContainingIgnoreCase("name");
    }

    @Test
    public void testSave (){
        PublisherRequest publisherRequest = new PublisherRequest("Pearson");

        when (publisherRepository.save(any(Publisher.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PublisherResponse result = publisherService.save(publisherRequest);

        assertEquals(publisherRequest.name(), result.name());
        verify(publisherRepository).save(any(Publisher.class));
    }

    @Test
    public void testSave_DuplicateName_ThrowsException() {
        PublisherRequest publisherRequest = new PublisherRequest("Pearson");
        when(publisherRepository.existsByName(publisherRequest.name())).thenReturn(true);

        assertThrows(BusinessException.class, () -> publisherService.save(publisherRequest));

        verify(publisherRepository, never()).save(any(Publisher.class));
    }


    @Test
    public void testUpdate(){
        PublisherRequest publisherRequest = new PublisherRequest("Pearson 2");
        Publisher publisher = DataProvider.publisherListMock().get(2);

        when (publisherRepository.findById(2L)).thenReturn(Optional.of(publisher));

        when(publisherRepository.save(any(Publisher.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PublisherResponse result= publisherService.update(2L, publisherRequest);

        assertEquals(publisherRequest.name(), result.name());
        verify(publisherRepository).findById(2L);
        verify(publisherRepository).save(any(Publisher.class));
    }

    @Test
    public void testDelete_ok (){
        Publisher publisher = new Publisher("Pearson");

        when (publisherRepository.findById(2L))
                .thenReturn(Optional.of(publisher));
        publisherService.deleteById(2L);

        verify(publisherRepository).findById(2L);
        verify(publisherRepository).delete(any(Publisher.class));
    }

    @Test
    public void testDelete_businessException (){
        Publisher publisher = new Publisher("Pearson");

        when (publisherRepository.findById(2L)).thenReturn(Optional.of(publisher));
        when(publisherRepository.existsBookByPublisherId(2L)).thenReturn(true);

        assertThrows(BusinessException.class, ()->publisherService.deleteById(2L));
        verify(publisherRepository).findById(2L);
        verify(publisherRepository, never()).delete(any(Publisher.class));
    }

    @Test
    void getPublisherOrThrow_ok() {
        Publisher publisher = DataProvider.publisherListMock().get(0);

        when(publisherRepository.findById(0L))
                .thenReturn(Optional.of(publisher));

        Publisher result = publisherService.getPublisherOrThrow(0L);

        assertNotNull(result);
        assertEquals(publisher.getName(), result.getName());
        verify(publisherRepository).findById(0L);
    }

    @Test
    public void testGetPublisherOrThrow_notFound(){
        when(publisherRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> publisherService.getPublisherOrThrow(2L));
    }
}
