package com.library.loansystem.Services;

import com.library.loansystem.Entities.Author;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    AuthorRepository authorRepository;

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Author findById(Long id) {
        return authorRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Resource not found 404"));
    }

    @Override
    public Author save(Author author) {
        return authorRepository.save(author);
    }

    @Override
    public void deleteById(Long id) {
        Author author = findById(id);
        authorRepository.delete(author);
    }

    @Override
    public Author update(Long id, Author authorAux) {
        Author author = findById(id);
        author.setName(author.getName());
        author.setLastName(authorAux.getLastName());
        author.setNationality(authorAux.getNationality());

        return authorRepository.save(author);
    }
}
