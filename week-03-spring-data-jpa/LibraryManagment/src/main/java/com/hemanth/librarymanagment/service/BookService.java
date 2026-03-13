package com.hemanth.librarymanagment.service;

import com.hemanth.librarymanagment.Interfaces.BookInterface;
import com.hemanth.librarymanagment.dto.AuthorDto;
import com.hemanth.librarymanagment.dto.BookDto;
import com.hemanth.librarymanagment.entity.AuthorEntity;
import com.hemanth.librarymanagment.entity.BookEntity;
import com.hemanth.librarymanagment.exception.ResourceAlreadyExistException;
import com.hemanth.librarymanagment.exception.ResourceNotFoundException;
import com.hemanth.librarymanagment.repository.AuthorEntityRepository;
import com.hemanth.librarymanagment.repository.BookEntityRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService implements BookInterface {

    private final BookEntityRepository bookRepository;
    private final AuthorEntityRepository authorRepository;
    private final ModelMapper modelMapper;

    public BookService(BookEntityRepository bookRepository,
                       AuthorEntityRepository authorRepository,
                       ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {

        if(bookRepository.existsByIsbn(bookDto.getIsbn())){
            throw new ResourceAlreadyExistException("ISBN already exists: " + bookDto.getIsbn());
        }

        BookEntity bookEntity = modelMapper.map(bookDto, BookEntity.class);

        // Attach authors in relationship
        if(!bookDto.getAuthorIds().isEmpty()){
            Set<AuthorEntity> authors = new HashSet<>();
            for(Long id : bookDto.getAuthorIds()){
                AuthorEntity author = authorRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Author not found id = " + id));
                authors.add(author);
                author.getBookEntities().add(bookEntity); // maintain sync
            }
            bookEntity.setAuthorEntities(authors);
        }
        BookEntity saved = bookRepository.saveAndFlush(bookEntity);
        BookDto bookDto1=modelMapper.map(saved,BookDto.class);
        bookDto1.setCreatedAt(saved.getCreatedAt());
        bookDto1.setUpdateAt(saved.getUpdateAt());
        bookDto1.setAuthorIds(bookDto.getAuthorIds());
        return bookDto1;
    }

    @Override
    public BookDto getBookById(Long id) {
        BookEntity entity = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found id = " + id));
        return modelMapper.map(entity, BookDto.class);
    }


    @Override
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> {
                    BookDto dto = modelMapper.map(book, BookDto.class);

                    // Extract authorIds only from THIS book
                    Set<Long> authorIds = book.getAuthorEntities()
                            .stream()
                            .map(author -> author.getAuthorId())
                            .collect(Collectors.toSet());

                    dto.setAuthorIds(authorIds);
                    return dto;
                })
                .toList();
    }

    @Override
    public void deleteBookById(Long id) {
        BookEntity entity = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found id = " + id));

        // Remove relationship before delete
        entity.getAuthorEntities().forEach(author ->
                author.getBookEntities().remove(entity));

        bookRepository.delete(entity);
    }

    @Override
    public List<BookDto> getBooksByDate(LocalDate date) {
        return bookRepository.findByPublishDate(date.atStartOfDay()).stream()
                .map(b -> modelMapper.map(b, BookDto.class))
                .toList();
    }

    @Override
    public List<BookDto> findBookByTitle(String title) {
        List<BookDto> bookDtoList = bookRepository.findBookByBookTitle(title).stream().map(entity->modelMapper.map(entity,BookDto.class)).toList(); // (Title column missing in entity - add later)
        if(bookDtoList.isEmpty())
            throw new ResourceNotFoundException("Book with title "+title);
        return bookDtoList;
    }

    @Override
    public List<BookDto> findAllBookByAuthor(String authorName) {
        return bookRepository.findByAuthorEntitiesAuthorName(authorName).stream()
                .map(b -> modelMapper.map(b, BookDto.class))
                .toList();
    }
     public boolean isExistById(Long id){
        java.util.Optional<BookEntity> bookEntity=bookRepository.findById(id);
        if(bookEntity.isEmpty())
            throw new ResourceNotFoundException("Book id not found "+id);
        else
            return true;
     }
    @Override
    public BookDto updateBook(Long id, BookDto bookDto) {
        BookEntity bookEntity;
       if(isExistById(id)) {
           bookEntity = modelMapper.map(bookDto, BookEntity.class);
           bookRepository.save(bookEntity);
       }
       return bookDto;
    }
}
