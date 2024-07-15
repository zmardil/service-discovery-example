package com.zmardil.author_service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/authors")
public class AuthorController {

    private List<Author> authors = List.of(
        new Author(1, "Robert C. Martin"), 
        new Author(2, "Erich Gamma")
    );

    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Integer id) {
        Optional<Author> authorOptional = authors.stream().filter(author -> author.id().equals(id)).findFirst();
        return authorOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    
}

record Author(Integer id, String name) {
}
