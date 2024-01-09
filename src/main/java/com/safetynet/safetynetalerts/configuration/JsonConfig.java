package com.safetynet.safetynetalerts.configuration;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JsonConfig {

	@Bean
	DataStore dataStore(ObjectMapper objectMapper) throws IOException {
		File file = new File("src/main/resources/data.json");
		return objectMapper.readValue(file, DataStore.class);
	}

	@Bean
	ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}