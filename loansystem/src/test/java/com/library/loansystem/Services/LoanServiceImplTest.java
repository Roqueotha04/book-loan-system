package com.library.loansystem.Services;

import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Loan;
import com.library.loansystem.Mapper.LoanMapper;
import com.library.loansystem.Repositories.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.crypto.Data;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    private LoanServiceImpl loanService;

    @BeforeEach
    void setUp (){
        LoanMapper loanMapper = new LoanMapper();
        loanService = new LoanServiceImpl(loanRepository, loanMapper);
    }

    @Test
    public void testFindAll(){
        List<Loan> loanList = DataProvider.loanListMock();

        when(loanRepository.findAll())
                .thenReturn(loanList);

        List<LoanResponse> result = loanService.findAll();

        assertEquals(loanList.size(), result.size());
        assertEquals(loanList.get(1).getBook().getName(), result.get(1).bookName());
        assertEquals(loanList.get(1).getUser().getUsername(), result.get(1).username());
        verify(loanRepository).findAll();
    }

    @Test
    public void testFindActiveLoans(){
        List<Loan> loanList = DataProvider.loanListMock();
        loanList.get(1).setActive(false);

        when(loanRepository.findAll())
                .thenReturn(loanList);

        List<LoanResponse> result = loanService.findActiveLoans();

        assertEquals(loanList.stream().filter(Loan::getActive).count(), result.size());

        verify(loanRepository).findAll();
    }



    @Test
    public void testExistsActiveLoanByUserId(){
        when(loanRepository.existsByUserIdAndActiveTrue(1L))
                .thenReturn(true);

        Boolean result = loanService.existsActiveLoanByUserId(1L);

        assertTrue(result);
        verify(loanRepository).existsByUserIdAndActiveTrue(1L);
    }

    @Test
    public void testExistsActiveLoanByBookId(){
        when(loanRepository.existsByBookIdAndActiveTrue(1L))
                .thenReturn(true);

        Boolean result = loanService.existsActiveLoanByBookId(1L);

        assertTrue(result);
        verify(loanRepository).existsByBookIdAndActiveTrue(1L);
    }
}
