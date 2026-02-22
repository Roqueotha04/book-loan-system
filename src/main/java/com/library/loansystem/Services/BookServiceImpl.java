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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
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
    @Transactional
    public BookResponse save(BookRequest bookRequest) {
        Book book = toBook(bookRequest);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public BookResponse update(Long id, BookRequest bookRequest) {
        Book book = getBookOrThrow(id);
        book.setName(bookRequest.name());
        book.setGenre(bookRequest.genre());
        book.setIsbn(bookRequest.isbn());

        if (!book.getPublisher().getId().equals(bookRequest.publisherID())) {
            Publisher newPublisher = publisherService.getPublisherOrThrow(bookRequest.publisherID());
            book.setPublisher(newPublisher);
        }
        updateAuthors(book, bookRequest.authorsIds());
       return bookMapper.toResponse(bookRepository.save(book));
    }

    private void updateAuthors(Book book, List<Long> authorIds){
        book.getAuthorXBooks().clear();

        authorIds.forEach(authorId->{
            Author author = authorService.getAuthorOrThrow(authorId);
            book.getAuthorXBooks().add(new AuthorXBook(book, author));
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Book book = getBookOrThrow(id);

        if (bookRepository.hasCopies(id)) throw  new BusinessException("Cannot delete a book with existing copies");

        bookRepository.delete(book);
    }

    @Override
    @Transactional
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
        Author author = authorService.getAuthorOrThrow(authorId);
        return bookRepository.findByAuthorXBooks_Author_Id(author.getId()).stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }


    public BookResponse findByIsbn(String isbn){
        Book book=bookRepository.findByIsbn(isbn).orElseThrow(()-> new ResourceNotFoundException("Book not found with isbn: " +isbn));
        return bookMapper.toResponse(book);
    }

    private Book toBook (BookRequest bookRequest){
        Book book = new Book();
        book.setName(bookRequest.name());
        book.setGenre(bookRequest.genre());
        book.setIsbn(bookRequest.isbn());
        book.setBookCopyList(new ArrayList<>());
        //Publisher
        Publisher publisher =  publisherService.getPublisherOrThrow(bookRequest.publisherID());
        book.setPublisher(publisher);
        //Author
        List<AuthorXBook> authorXBooks = bookRequest.authorsIds().stream()
                .map(id -> {
                    Author author = authorService.getAuthorOrThrow(id);
                    return new AuthorXBook(book, author);
                }).toList();
        book.setAuthorXBooks(authorXBooks);

        return book;
    }
}
