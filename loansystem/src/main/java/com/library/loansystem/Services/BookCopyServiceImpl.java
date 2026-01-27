package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.BookCopyRequest;
import com.library.loansystem.DTO.Response.BookCopyResponse;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.BookCopyMapper;
import com.library.loansystem.Repositories.BookCopyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCopyServiceImpl implements BookCopyService{
    private final BookCopyRepository bookCopyRepository;
    private final BookCopyMapper bookCopyMapper;
    private final BookService bookService;

    public BookCopyServiceImpl(BookCopyRepository bookCopyRepository, BookCopyMapper bookCopyMapper, BookService bookService) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookCopyMapper = bookCopyMapper;
        this.bookService = bookService;
    }


    @Override
    public List<BookCopyResponse> findAll() {
        return bookCopyRepository.findAll().stream()
                .map(bookCopyMapper::toResponse)
                .toList();
    }

    @Override
    public List<BookCopyResponse> findAllByBook(String Isbn) {
        return bookCopyRepository.findByBookIsbn(Isbn).stream()
                .map(bookCopyMapper::toResponse)
                .toList();
    }


    @Override
    public List<BookCopyResponse> findAvailableByBook(String Isbn) {
        return bookCopyRepository.findByBookIsbnAndState(Isbn, BookCopyState.AVAILABLE).stream()
                .map(bookCopyMapper::toResponse)
                .toList();
    }

    @Override
    public BookCopyResponse save(BookCopyRequest bookCopyRequest) {
        Book book = bookService.getBookOrThrow(bookCopyRequest.bookId());
        BookCopy bookCopy = new BookCopy(book, bookCopyRequest.state());
        return bookCopyMapper.toResponse(bookCopyRepository.save(bookCopy));
    }

    @Override
    public BookCopyResponse patchState(Long id, BookCopyState bookCopyState) {
        BookCopy bookCopy = getBookCopyOrThrow(id);
        bookCopy.setState(bookCopyState);
        return bookCopyMapper.toResponse(bookCopyRepository.save(bookCopy));
    }

    @Override
    public void delete(Long id) {
        BookCopy bookCopy = getBookCopyOrThrow(id);
        if (bookCopy.getState().equals(BookCopyState.LOANED)) throw new BusinessException("Cannot delete a loaned copy");
        bookCopyRepository.delete(bookCopy);
    }


    @Override
    public BookCopy selectAvailableCopyOrThrow(Long bookId) {
        return bookCopyRepository
                .findFirstByBookIdAndState(bookId, BookCopyState.AVAILABLE)
                .orElseThrow(() -> new BusinessException("No available copies for this book"));
    }

    @Override
    public BookCopy getBookCopyOrThrow(Long id) {
        return bookCopyRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Could not found BookCopy with id: " +id));
    }


}
