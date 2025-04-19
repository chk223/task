package com.example.task.domain.auth.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class RefreshTokenRepository {

    Map<Long, String> refreshStorage = new HashMap<>();

    public String save(Long id, String token) {
        refreshStorage.put(id,token);
        return token;
    }
}
