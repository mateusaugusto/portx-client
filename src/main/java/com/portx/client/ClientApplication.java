package com.portx.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.IntStream;

@SpringBootApplication
public class ClientApplication {

	private static final Logger log = Logger.getLogger(ClientApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) {
		return args -> {
			String apiCreateUrl
					= "http://localhost:8086/payments";

			String apiFindAllCreatedUrl
					= "http://localhost:8086/payments/search?status=CREATED";

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(List.of(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);

			log.info("#### Posting 100 payments ###");

			IntStream.range(0, 100).forEach(index -> {
				headers.set("idempotency-Key", UUID.randomUUID().toString());

				HttpEntity<String> entity = new HttpEntity<>(this.buildJsonCreate(), headers);
				ResponseEntity<String> response
						= restTemplate.postForEntity(apiCreateUrl, entity, String.class);
				log.info("Payment created -> " + response.getBody());
			});

			log.info("");
			log.info("");
			log.info("");


			log.info("#### Retrieve CREATED payments ###");


			ResponseEntity<String> response
					= restTemplate.getForEntity(apiFindAllCreatedUrl, String.class);

			log.info(response.getBody());
		};
	}

	private String buildJsonCreate(){
		return "{\n" +
				"    \"currency\": \"USD\",\n" +
				"    \"amount\": 15,\n" +
				"    \"originator\": {\n" +
				"        \"name\": \"originator\"\n" +
				"    },\n" +
				"    \"beneficiary\": {\n" +
				"        \"name\": \"beneficiary\"\n" +
				"    },\n" +
				"    \"receiver\": {\n" +
				"        \"type\": \"receiver\",\n" +
				"        \"number\": 123\n" +
				"    },\n" +
				"    \"sender\": {\n" +
				"        \"type\": \"sender\",\n" +
				"        \"number\": 123\n" +
				"    }\n" +
				"}";
	}

}
