package com.example.treasuremap.port.adapter.service;

import com.example.treasuremap.domain.model.IDGeneratorService;

import java.util.UUID;

public class UUIDGeneratorService implements IDGeneratorService {
    public String generateID() {
        return UUID.randomUUID().toString().toUpperCase().substring(0, 8);
    }
}

