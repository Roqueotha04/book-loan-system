package com.library.loansystem.Services;

import com.library.loansystem.DTO.AuthorRequest;
import com.library.loansystem.DTO.AuthorResponse;
import com.library.loansystem.Entities.Author;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Repositories.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public List<AuthorResponse> findAll() {
            return authorRepository.findAll().stream()
                    .map(this::toResponse)
                    .toList();
    }

    @Override
    public AuthorResponse findById(Long id) {
        return toResponse(getAuthorOrThrow(id));
    }

    @Override
    public AuthorResponse save(AuthorRequest authorAux) {
        Author author = new Author(authorAux.getName(), authorAux.getLastName(), authorAux.getNationality());
        return toResponse(authorRepository.save(author));
    }

    @Override
    public void deleteById(Long id) {
        Author author = getAuthorOrThrow(id);
        authorRepository.delete(author);
    }

    @Override
    public AuthorResponse update(Long id, AuthorRequest authorAux) {
        Author author = getAuthorOrThrow(id);
        author.setName(authorAux.getName());
        author.setLastName(authorAux.getLastName());
        author.setNationality(authorAux.getNationality());

        return toResponse(authorRepository.save(author));
    }

    //Private utility methods
    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getLastName(),
                author.getNationality()
        );
    }

    private Author getAuthorOrThrow(Long id){
        return authorRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Author not found with id: " +id));
    }
}
