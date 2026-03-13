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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BookService implements BookInterface {

    private final BookEntityRepository bookRepository;
    private final AuthorEntityRepository authorRepository;
    private final ModelMapper modelMapper;
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
            bookEntity.setAuthorIds(authors);
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
                    BookDto dto = new BookDto();
                    dto.setBookId(book.getBookId());
                    dto.setBookTitle(book.getBookTitle());
                    dto.setIsbn(book.getIsbn());
                    dto.setPrice(book.getPrice());
                    dto.setPublishDate(book.getPublishDate());
                    dto.setCreatedAt(book.getCreatedAt());
                    dto.setUpdateAt(book.getUpdateAt());

                    Set<Long> authorIds = book.getAuthorIds()
                            .stream()
                            .map(AuthorEntity::getAuthorId)
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
        entity.getAuthorIds().forEach(author ->
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
        return bookRepository.findByAuthorIdsAuthorName(authorName).stream()
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

    @Override
    @Transactional
    public BookDto updatePartialBook(Long id, Map<String, Object> updates) {

        BookEntity bookEntity = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        Set<Long> ids=new HashSet<>();
        updates.forEach((fieldName, value) -> {

            // ❌ protect immutable fields
            if (fieldName.equalsIgnoreCase("id")
                    || fieldName.equalsIgnoreCase("bookId")
                    || fieldName.equalsIgnoreCase("createdAt")
                    || fieldName.equalsIgnoreCase("updatedAt")) {
                throw new UnsupportedOperationException(fieldName + " cannot be updated");
            }

            // ✅ HANDLE RELATIONSHIP SEPARATELY
            if (fieldName.equals("authorIds")) {

                Collection<?> rawIds = (Collection<?>) value;

                for (Object o : rawIds) {
                    ids.add(((Number) o).longValue()); // ⭐ THIS IS THE FIX
                }

                Set<AuthorEntity> authors = new HashSet<>();
                for (Long authorId : ids) {
                    AuthorEntity author = authorRepository.findById(authorId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Author not found id = " + authorId));
                    authors.add(author);
                    author.getBookEntities().add(bookEntity); // maintain sync
                }

                bookEntity.setAuthorIds(authors);
                return; // ⭐ DO NOT fall into reflection
            }

            // ✅ NORMAL FIELD UPDATE
            Field field = ReflectionUtils.findField(BookEntity.class, fieldName, null);
            if (field == null) {
                throw new IllegalArgumentException("Invalid field: " + fieldName);
            }

            field.setAccessible(true);
            ReflectionUtils.setField(field, bookEntity, value);
        });

        BookEntity saved = bookRepository.saveAndFlush(bookEntity);
        BookDto bookDto=new BookDto();
        bookDto.setBookId(saved.getBookId());
        bookDto.setBookTitle(saved.getBookTitle());
        bookDto.setIsbn(saved.getIsbn());
        bookDto.setUpdateAt(saved.getUpdateAt());
        bookDto.setCreatedAt(saved.getCreatedAt());
        bookDto.setPrice(saved.getPrice());
        bookDto.setPublishDate(saved.getPublishDate());
        bookDto.setAuthorIds(ids);
       return bookDto;
    }
}


