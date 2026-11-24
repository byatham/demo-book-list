package com.sd.tech.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sd.tech.entity.Book;

@RestController
@RequestMapping("/book")
public class BookController {
	
private List<Book> listOfBooks=new ArrayList<>();
	
	@PostMapping
	public Book addBook(@RequestBody Book book)
	{
		listOfBooks.add(book);
		return book;
	}
	

	@GetMapping
	public List<Book> getBooks()
	{
		
		return listOfBooks;
	}


	@GetMapping("/{id}")
	public Book getBookById(@PathVariable int id)
	{
		
		return listOfBooks.get(id-1);
	}

}
