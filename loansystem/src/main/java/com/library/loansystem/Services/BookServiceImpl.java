package com.library.loansystem.Services;

import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;
import com.library.loansystem.Entities.Enums.BookGenre;
import com.library.loansystem.Mapper.BookMapper;
import com.library.loansystem.DTO.Request.BookRequest;
import com.library.loansystem.DTO.Response.BookResponse;
import com.library.loansystem.Entities.Author;
import com.library.loansystem.Entities.AuthorXBook;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.Publisher;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService{

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final LoanService loanService;
    private final PublisherService publisherService;
    private final BookCopyService bookcopyService;
    private final AuthorService authorService;

    public BookServiceImpl(BookMapper bookMapper, BookRepository bookRepository, LoanService loanService, PublisherService publisherService, BookCopyService bookcopyService, AuthorService authorService) {
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
        this.loanService = loanService;
        this.publisherService = publisherService;
        this.bookcopyService = bookcopyService;
        this.authorService = authorService;
    }

    @Override
    public List<BookResponse> findAll() {
       return bookRepository.findAll().stream()
               .map(bookMapper::toResponse)
               .toList();
    }

    @Override
    public BookResponse findById(Long id) {
        return bookMapper.toResponse(getBookOrThrow(id));
    }

    @Override
    public BookResponse save(BookRequest bookRequest) {
        Book book = toBook(bookRequest);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Override
    public BookResponse update(Long id, BookRequest bookRequest) {
        Book book = getBookOrThrow(id);
        book.setName(bookRequest.getName());
        book.setGenre(bookRequest.getGenre());
        book.setIsbn(bookRequest.getIsbn());
       return bookMapper.toResponse(bookRepository.save(book));
    }

    /// ?
    @Override
    public void delete(Long id) {
        Book book = getBookOrThrow(id);
        if(loanService.existsActiveLoanByBookId(book.getId())) throw new BusinessException("Cannot delete a book with active Loans");
        bookRepository.delete(book);
    }

    public BookResponse changeStatus (Long id){
        Book book = getBookOrThrow(id);
        book.setActive(!book.getActive());
        return bookMapper.toResponse(bookRepository.save(book));
    }


    public Book getBookOrThrow (Long id){
        return bookRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Book not found with id: " +id));
    }

    @Override
    public List<BookResponse> findByNameContaining(String name) {
        return bookRepository.findByNameContainingIgnoreCase(name).stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Override
    public List<BookResponse> findByGenre(BookGenre genre) {
        return bookRepository.findByGenre(genre).stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Override
    public List<BookResponse> findByAuthor(Long authorId) {
        return bookRepository.findByAuthorXBooks_Author_Id(authorId).stream()
                .map(bookMapper::toResponse)
                .toList();
    }


    public BookResponse findByIsbn(String isbn){
        Book book=bookRepository.findByIsbn(isbn).orElseThrow(()-> new ResourceNotFoundException("Book not found with isbn: " +isbn));
        return bookMapper.toResponse(book);
    }

    private Book toBook (BookRequest bookRequest){
        Book book = new Book();
        book.setName(bookRequest.getName());
        book.setGenre(bookRequest.getGenre());
        book.setIsbn(bookRequest.getIsbn());
        book.setBookCopyList(new ArrayList<>());
        //Publisher
        Publisher publisher =  publisherService.getPublisherOrThrow(bookRequest.getPublisherID());
        book.setPublisher(publisher);
        //Author
        List<AuthorXBook> authorXBooks = bookRequest.getAuthorsIds().stream()
                .map(id -> {
                    Author author = authorService.getAuthorOrThrow(id);
                    return new AuthorXBook(book, author);
                }).toList();
        book.setAuthorXBooks(authorXBooks);

        return book;
    }
}
