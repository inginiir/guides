package com.kalita.controllers;

import com.kalita.model.Attribute;
import com.kalita.service.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/")
public class DomainController {

    private final AttributeService attributeService;

    @Autowired
    public DomainController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @PostMapping("/switch")
    public ResponseEntity<Boolean> getQuery(@RequestBody Attribute attribute) {
        return ResponseEntity.ok(attributeService.sendQueryEnum(attribute));
    }
}
