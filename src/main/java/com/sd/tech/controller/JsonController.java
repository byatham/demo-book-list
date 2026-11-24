package com.sd.tech.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.sd.tech.service.JsonFileService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/json")
public class JsonController {

    private final JsonFileService jsonFileService;

    public JsonController(JsonFileService jsonFileService) {
        this.jsonFileService = jsonFileService;
    }

    @GetMapping("/read")
    public JsonNode readJson() {

         JsonNode jsondata = jsonFileService.readJsonFile();
         System.out.println("jsondata : "+jsondata);
         return jsondata;
    }
}