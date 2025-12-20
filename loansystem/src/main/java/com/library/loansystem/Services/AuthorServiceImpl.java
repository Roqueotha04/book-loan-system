package com.library.loansystem.Services;

import com.library.loansystem.DTO.AuthorRequest;
import com.library.loansystem.DTO.AuthorResponse;
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
    public List<AuthorResponse> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public AuthorResponse findById(Long id) {
        return authorRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Resource not found - 404"));
    }

    @Override
    public AuthorResponse save(AuthorRequest author) {
        return authorRepository.save(author);
    }

    @Override
    public void deleteById(Long id) {
        Author author = findById(id);
        authorRepository.delete(author);
    }

    @Override
    public AuthorResponse update(Long id, AuthorResponse authorAux) {
        Author author = findById(id);
        author.setName(author.getName());
        author.setLastName(authorAux.getLastName());
        author.setNationality(authorAux.getNationality());

        return authorRepository.save(author);
    }
}
