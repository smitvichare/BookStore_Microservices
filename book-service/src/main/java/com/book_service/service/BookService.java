package com.book_service.service;

import com.book_service.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BookService {
    Book addBook(Book book);

    List<Book> getAllBookDetails();
    Book getBookDetailsById(Long id);
    ResponseEntity<?> deleteBookDetailsById(Long id);


    ResponseEntity<?> ChangeBookQuantity(Long bookid,Long quantity);
    ResponseEntity<?> ChangeBookPrice(Long bookid,Long price);

    ResponseEntity<?> addBackToBookTable(Long bookId,Long qty);

    ResponseEntity<?> orderPlacedUpdateBookTable(Long bookId, Long qty);
}
