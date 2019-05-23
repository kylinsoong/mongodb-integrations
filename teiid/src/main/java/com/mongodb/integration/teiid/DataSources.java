package com.mongodb.integration.teiid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.teiid.spring.data.mongodb.MongoDBConnectionFactory;

@Configuration
public class DataSources {

	@Bean
	public MongoDBConnectionFactory accounts(@Autowired MongoTemplate template) {
		return new MongoDBConnectionFactory(template);
	}
}
