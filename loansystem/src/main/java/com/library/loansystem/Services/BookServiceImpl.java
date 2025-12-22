package com.library.loansystem.Services;

import com.library.loansystem.DTO.Author.AuthorResponse;
import com.library.loansystem.DTO.Book.BookMapper;
import com.library.loansystem.DTO.Book.BookRequest;
import com.library.loansystem.DTO.Book.BookResponse;
import com.library.loansystem.DTO.PublisherResponse;
import com.library.loansystem.Entities.Author;
import com.library.loansystem.Entities.AuthorXBook;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.Publisher;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService{

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final PublisherService publisherService;
    private final AuthorService authorService;

    public BookServiceImpl(BookMapper bookMapper, BookRepository bookRepository, PublisherService publisherService, AuthorService authorService) {
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
        this.publisherService = publisherService;
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
        book.setStock(bookRequest.getStock());
       return bookMapper.toResponse(bookRepository.save(book));
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public BookResponse updateStock(Long id, int newStock) {
        return null;
    }

    public Book getBookOrThrow (Long id){
        return bookRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Book not found with id: " +id));
    }

    public Book toBook (BookRequest bookRequest){
        Book book = new Book();
        book.setName(bookRequest.getName());
        book.setGenre(bookRequest.getGenre());
        book.setStock(bookRequest.getStock());
        book.setLoanList(new ArrayList<>());
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
