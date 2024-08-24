package com.tartayadir.cryptoservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/message")
public class MessageController {

    @GetMapping("/{id}")
    public ResponseEntity<?> getMessage(@PathVariable String id, @RequestParam String key) {
        return ResponseEntity.ok("Ok");
    }
}
