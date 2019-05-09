package com.mongodb.integration.springdata;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.MongoException;

@SpringBootApplication
public class SpringDataApplication implements CommandLineRunner {
	
	@Autowired
	private CustomerRepository repository;
	
	@Autowired
	private MongoOperations operation;


	public static void main(String[] args) {
		SpringApplication.run(SpringDataApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		
		
		readExists();
		
		writeAndRead();

		
	}


	private void readExists() {

		Query query  = new Query();
		operation.exists(query, "cities");
		operation.executeQuery(query, "cities", new DocumentCallbackHandler() {

			@Override
			public void processDocument(Document document) throws MongoException, DataAccessException {
				document.values().forEach(c -> {
					System.out.println(c);
				});
			}});
	}


	private void writeAndRead() {

		repository.deleteAll();
		
		repository.save(new Customer("Alice", "Smith"));
		repository.save(new Customer("Bob", "Smith"));
		
		System.out.println("Customers found with findAll():");
		System.out.println("-------------------------------");
		for (Customer customer : repository.findAll()) {
			System.out.println(customer);
		}
		System.out.println();
		
		System.out.println("Customer found with findByFirstName('Alice'):");
		System.out.println("--------------------------------");
		System.out.println(repository.findByFirstName("Alice"));

		System.out.println("Customers found with findByLastName('Smith'):");
		System.out.println("--------------------------------");
		for (Customer customer : repository.findByLastName("Smith")) {
			System.out.println(customer);
		}
	}

}
