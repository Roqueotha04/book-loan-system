package com.library.loansystem.Initializer;

import com.library.loansystem.Entities.*;
import com.library.loansystem.Entities.Enums.BookGenre;
import com.library.loansystem.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DataLoader  implements ApplicationRunner{

        @Autowired AuthorRepository authorRepository;
        @Autowired BookRepository bookRepository;
        @Autowired UserRepository userRepository;
        @Autowired LoanRepository loanRepository;
        @Autowired AuthorXBookRepository authorXBookRepository;
        @Autowired PublisherRepository publisherRepository;

        @Override
        public void run (ApplicationArguments args) {
            loadPublishers();
            loadAuthors();
            loadBooks();
            loadUsers();
            loadLoans();

        }

        private void loadPublishers() {
                if (publisherRepository.count() == 0) {
                    List<Publisher> publishers = List.of(
                            new Publisher("Penguin Random House"),
                            new Publisher("HarperCollins"),
                            new Publisher("Simon & Schuster"),
                            new Publisher("Hachette Book Group"),
                            new Publisher("Macmillan Publishers"),
                            new Publisher("Oxford University Press"),
                            new Publisher("Cambridge University Press"),
                            new Publisher("Pearson"),
                            new Publisher("Scholastic"),
                            new Publisher("Bloomsbury Publishing")
                    );
                    publisherRepository.saveAll(publishers);
                }
        }
        private void loadAuthors() {
            if (authorRepository.count() == 0) {
                List<Author> authors = List.of(
                        new Author("George", "Orwell", "British"),
                        new Author("J.K.", "Rowling", "British"),
                        new Author("J.R.R.", "Tolkien", "British"),
                        new Author("Isaac", "Asimov", "American"),
                        new Author("Stephen", "King", "American"),
                        new Author("Agatha", "Christie", "British"),
                        new Author("Jane", "Austen", "British"),
                        new Author("Mariano", "Sigman", "Argentinian"),
                        new Author("Santiago", "Bilinkis", "Argentinian"),
                        new Author("Yuval", "Harari", "Israeli")
                );
                authorRepository.saveAll(authors);
            }
        }
        private void loadBooks() {

        if (bookRepository.count() > 0) {
            return;
        }

        List<Author> authors = authorRepository.findAll();
        List<Publisher> publishers = publisherRepository.findAll();

        Book book1 = new Book("1984", 15, BookGenre.FICTION, publishers.get(0));
        addAuthorToBook(book1, authors.get(0)); // Orwell

        Book book2 = new Book("Harry Potter and the Philosopher's Stone", 20, BookGenre.FANTASY, publishers.get(9));
        addAuthorToBook(book2, authors.get(1)); // Rowling

        Book book3 = new Book("The Lord of the Rings", 12, BookGenre.FANTASY, publishers.get(1));
        addAuthorToBook(book3, authors.get(2)); // Tolkien

        Book book4 = new Book("Foundation", 10, BookGenre.SCIENCE_FICTION, publishers.get(1));
        addAuthorToBook(book4, authors.get(3)); // Asimov

        Book book5 = new Book("The Shining", 8, BookGenre.HORROR, publishers.get(0));
        addAuthorToBook(book5, authors.get(4)); // King

        Book book6 = new Book("Murder on the Orient Express", 9, BookGenre.MYSTERY, publishers.get(1));
        addAuthorToBook(book6, authors.get(5)); // Christie

        Book book7 = new Book("Pride and Prejudice", 11, BookGenre.FICTION, publishers.get(0));
        addAuthorToBook(book7, authors.get(6)); // Austen

        Book book8 = new Book("Sapiens", 14, BookGenre.NON_FICTION, publishers.get(1));
        addAuthorToBook(book8, authors.get(9)); // Harari

        Book book9 = new Book("Artificial", 18, BookGenre.NON_FICTION, publishers.get(0));
        addAuthorToBook(book9, authors.get(7)); // Sigman
        addAuthorToBook(book9, authors.get(8)); // Bilinkis

        bookRepository.saveAll(List.of(
                book1, book2, book3, book4, book5,
                book6, book7, book8, book9
        ));
    }
        private void addAuthorToBook(Book book, Author author) {
        AuthorXBook link = new AuthorXBook();
        link.setBook(book);
        link.setAuthor(author);
        book.getAuthorXBooks().add(link);
    }
        private void loadUsers(){
            if (userRepository.count()==0){
                List<User> users = List.of(
                        new User("neo@gmail.com", "neo01", "matrix"),
                        new User("tonystark@gmail.com", "tony", "iron"),
                        new User("indianajones@gmail.com", "indy", "temple"),
                        new User("mcfly@gmail.com", "mcfly", "delorean"),
                        new User("tylerdurden@gmail.com", "tyler", "fightclub")
                );
                userRepository.saveAll(users);
            }
        }
        private void loadLoans() {

        if (loanRepository.count() > 0) {
            return;
        }

        List<User> users = userRepository.findAll();
        List<Book> books = bookRepository.findAll();

        List<Loan> loans = List.of(
                // Neo (3)
                new Loan(users.get(0), books.get(0)),
                new Loan(users.get(0), books.get(1)),
                new Loan(users.get(0), books.get(2)),

                // Tony Stark (3)
                new Loan(users.get(1), books.get(3)),
                new Loan(users.get(1), books.get(4)),
                new Loan(users.get(1), books.get(5)),

                // Indiana Jones (2)
                new Loan(users.get(2), books.get(6)),
                new Loan(users.get(2), books.get(7)),

                // Marty McFly (2)
                new Loan(users.get(3), books.get(1)),
                new Loan(users.get(3), books.get(8)),

                // Tyler Durden (2)
                new Loan(users.get(4), books.get(0)),
                new Loan(users.get(4), books.get(3))
        );

        loanRepository.saveAll(loans);
    }



}
