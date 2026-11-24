package com.sd.tech;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sd.tech.entity.Book;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import io.micrometer.core.instrument.MeterRegistry;

@SpringBootApplication
@RestController
@RequestMapping("/book")
public class DemoBooksListApplication {
	
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

	
	public static void main(String[] args) {
		SpringApplication.run(DemoBooksListApplication.class, args);
		System.out.println("url info : http://localhost:8082/actuator/health");
		System.out.println("Swagger endpoint: http://localhost:8082/swagger-ui/index.html");
		
		String addbook = """
								curl -X 'POST' \
				  'http://localhost:8082/book' \
				  -H 'accept: */*' \
				  -H 'Content-Type: application/json' \
				  -d '{
				  "id": 1,
				  "name": "AWS",
				  "authorName": "Balu"
				}'

							""";
		System.out.println("add book : "+addbook);

	}

	@Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", "demo-book-list");
    }

}
