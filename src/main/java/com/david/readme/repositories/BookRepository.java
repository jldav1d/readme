package com.david.readme.repositories;

import com.david.readme.models.Book;
import org.springframework.data.repository.ListCrudRepository;

public interface BookRepository extends ListCrudRepository<Book, Long> {

}
