package com.library.loansystem.Services;

import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Repositories.BookCopyRepository;
import com.library.loansystem.Repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCopyServiceImpl implements BookCopyService{
    private final BookCopyRepository bookCopyRepository;

    public BookCopyServiceImpl(BookCopyRepository bookCopyRepository) {
        this.bookCopyRepository = bookCopyRepository;
    }

    public List<BookCopyService> findAll(){
        return null;
    }

    @Override
    public BookCopy selectAvailableCopy(Long bookId) {
        return bookCopyRepository
                .findFirstByBookIdAndState(bookId, BookCopyState.AVAILABLE).orElseThrow(() -> new BusinessException("No copies available"));
    }
}
