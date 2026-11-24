package com.sd.tech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.micrometer.core.instrument.MeterRegistry;

@SpringBootApplication

public class DemoBooksListApplication {
	
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
