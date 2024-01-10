package com.safetynet.safetynetalerts.configuration;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JsonConfig {

	@Value("${json.file.path}")
	private String jsonFilePath;

	@Bean
	ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	DataStore dataStore(ObjectMapper objectMapper) throws IOException {
		File file = new File(jsonFilePath);
		return objectMapper.readValue(file, DataStore.class);
	}

}
