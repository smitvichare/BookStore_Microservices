package com.order_service.client;

import com.order_service.external.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "BOOK-MICROSERVICE")
public interface BookClient {

    @GetMapping("book/{id}")
    Book aParticularBook(@PathVariable Long id);

    @PutMapping("book/rmvFromCartAddToBook/{bookId}/{qty}")
    ResponseEntity<String> addedBackToBook(@PathVariable Long bookId, @PathVariable Long qty);

    @PutMapping("book/orderPlacedUpdateBookTable/{bookId}/{qty}")
    String placedOrderUpdateBookTable(@PathVariable Long bookId,@PathVariable Long qty);

}
