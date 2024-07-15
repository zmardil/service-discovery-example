package com.zmardil.book_service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/books")
public class BookController {

    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;
    private List<Book> bookList = List.of(
        new Book("978-0132350884", "Clean Code", 1),
        new Book("978-0201633610", "Design Patterns", 2)
    );


    public BookController(DiscoveryClient discoveryClient, RestClient.Builder resClientBuilder) {
        this.discoveryClient = discoveryClient;
        restClient = resClientBuilder.build();
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<BookWithAuthor> getBookByIdWithAuthor(@PathVariable String isbn) {
        Optional<Book> bookOptional = bookList.stream().filter(book -> book.isbn().equals(isbn))
            .findFirst();

        if(bookOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Book book = bookOptional.get();
        
        ServiceInstance serviceInstance = discoveryClient.getInstances("author-service").getFirst();
        if(serviceInstance == null) {
            return ResponseEntity.status(503).build(); // service unavailable
        }

        Author author;
        try {
            author = restClient.get().uri(serviceInstance.getUri() + "/authors/" + book.authorId())
                .retrieve()
                .body(Author.class);
        } catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(new BookWithAuthor(book.isbn(), book.title(), author));
    }
    
}

record BookWithAuthor(String isbn, String title, Author author) {

}

record Author(Integer id, String name) {

}

record Book(String isbn, String title, Integer authorId) {

}
