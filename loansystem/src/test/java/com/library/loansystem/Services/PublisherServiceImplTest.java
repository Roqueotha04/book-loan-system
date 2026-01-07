package com.library.loansystem.Services;


import com.library.loansystem.DTO.Response.PublisherResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Publisher;
import com.library.loansystem.Repositories.PublisherRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import java.util.List;

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

        assertEquals(publisherResponseList.get(1).getName(), result.get(1).getName());
        assertEquals(publisherResponseList.size(), result.size());

        verify(publisherRepository).findAll();
    }
}
