package com.sd.tech.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class JsonFileService {

    private static final Logger log =
            LoggerFactory.getLogger(JsonFileService.class);

    private final ObjectMapper objectMapper;

    public JsonFileService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode readJsonFile() {

        try {
            InputStream inputStream = new ClassPathResource(
                    "data/sample-data.json"
            ).getInputStream();

            JsonNode jsonData = objectMapper.readTree(inputStream);

            log.info("JSON File Data: {}", jsonData.toPrettyString());

            return jsonData;

        } catch (Exception e) {
            log.error("Error reading JSON file", e);
            throw new RuntimeException("Unable to read JSON file", e);
        }
    }
}