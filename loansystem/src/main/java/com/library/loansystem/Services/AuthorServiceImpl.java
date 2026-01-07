package com.library.loansystem.Services;

import com.library.loansystem.Mapper.AuthorMapper;
import com.library.loansystem.DTO.Request.AuthorRequest;
import com.library.loansystem.DTO.Response.AuthorResponse;
import com.library.loansystem.Entities.Author;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Repositories.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Override
    public List<AuthorResponse> findAll() {
            return authorRepository.findAll().stream()
                    .map(authorMapper::toResponse)
                    .toList();
    }

    @Override
    public AuthorResponse findById(Long id) {
        return authorMapper.toResponse(getAuthorOrThrow(id));
    }

    @Override
    public AuthorResponse save(AuthorRequest authorAux) {
        Author author = new Author(authorAux.getName(), authorAux.getLastName(), authorAux.getNationality());
        return authorMapper.toResponse(authorRepository.save(author));
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

        return authorMapper.toResponse(authorRepository.save(author));
    }

    public Author getAuthorOrThrow(Long id){
        return authorRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Author not found with id: " +id));
    }


}
