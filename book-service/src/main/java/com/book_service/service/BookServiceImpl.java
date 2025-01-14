package com.book_service.service;

import com.book_service.exception.BookNotFoundException;
import com.book_service.model.Book;
import com.book_service.repository.BookRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService{

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book addBook(Book book)  {

        return bookRepository.save(book);
    }

    @Override
    @CircuitBreaker(name = "bookBreaker",fallbackMethod = "bookBreakerFallback")
    public List<Book> getAllBookDetails() {
        return bookRepository.findAll();
    }

    public List<String> bookBreakerFallback(Exception e){
        List<String> list = new ArrayList<>();
        list.add("Sample Data");
        return list;
    }

    @Override
    @CircuitBreaker(name = "bookBreaker",fallbackMethod = "getBookDetailsFallback")
    public Book getBookDetailsById(Long bookId) {
        return bookRepository.findById(bookId).orElse(null);
    }

    public Book getBookDetailsFallback(Long bookId, Throwable throwable) {
        // Log the exception for debugging purposes
        System.err.println("Circuit breaker for getBookDetailsById: " + throwable.getMessage());

        // Return a default or null value as a fallback
        Book fallbackBook = new Book();
        fallbackBook.setId(bookId);
        fallbackBook.setBookName("Default Book Name");
        fallbackBook.setBookAuthor("Unknown Author");
        fallbackBook.setBookDescription("Book details are currently unavailable.");
        fallbackBook.setBookPrice(0);
        fallbackBook.setBookQuantity(0);

        return fallbackBook;
    }

    @Override
    public ResponseEntity<?> deleteBookDetailsById(Long bookId) {

        Book book = bookRepository.findById(bookId).orElse(null);
        if(book != null){
            bookRepository.deleteById(bookId);
            return ResponseEntity.ok("Book deleted Successfully");
        }else{
            return ResponseEntity.ok("Book id not found");
        }
    }

    @Override
    public ResponseEntity<?> ChangeBookQuantity(Long bookId, Long quantity) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isEmpty())
            return ResponseEntity.ok("Book not found");

        Book book = optionalBook.get();
        book.setBookQuantity(quantity);
        return ResponseEntity.ok(bookRepository.save(book));

    }

    @Override
    public ResponseEntity<?> ChangeBookPrice(Long bookId, Long price) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if(optionalBook.isEmpty())
            return ResponseEntity.ok("Book not found");

        Book book = optionalBook.get();
        book.setBookPrice(price);
        //bookRepository.save(book);
        return ResponseEntity.ok(bookRepository.save(book));
    }



    @Override
    @CircuitBreaker(name = "bookTableBreaker", fallbackMethod = "addBackToBookTableFallback")
    public ResponseEntity<?> addBackToBookTable(Long bookId,Long qty){

        Book books = bookRepository.findById(bookId).orElseThrow(()-> new BookNotFoundException("Book not found"));
        if(books!=null) {
            books.setBookQuantity(books.getBookQuantity()+qty);
            return ResponseEntity.ok(bookRepository.save(books));
        } else {
            return ResponseEntity.ok("book id not found");
        }
    }

    public String addBackToBookTableFallback(Long bookId, Long qty, Throwable throwable) {

        System.err.println("Circuit breaker triggered for addBackToBookTable: " + throwable.getMessage());
        return "Service is currently unavailable. Unable to add back to the book table at the moment.";
    }


    @Override
    @CircuitBreaker(name = "orderBreaker", fallbackMethod = "orderPlacedFallback")
    public ResponseEntity<?> orderPlacedUpdateBookTable(Long bookId, Long qty){
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new BookNotFoundException("Book not found"));

        if (book==null) {
            return ResponseEntity.ok("book not available...");
        } else {
            Long afterPlacedOrder = book.getBookQuantity() - qty;
            if(afterPlacedOrder >= 0){
                book.setBookQuantity(afterPlacedOrder); //decrease qty from book table
                return ResponseEntity.ok(bookRepository.save(book));
            }else{
                return ResponseEntity.ok("Available : "+ book.getBookQuantity()+"\nRequired : "+qty);
            }
        }
    }

    public String orderPlacedFallback(Long bookId, Long qty, Throwable throwable) {
        System.err.println("Circuit breaker triggered for orderPlacedUpdateBookTable: " + throwable.getMessage());
        return "Service is currently unavailable. Unable to place the order at this time. Please try again later.";
    }

}