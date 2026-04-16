package com.dave.hngstage1.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class NationalizeClient {
    private final RestTemplate restTemplate=new RestTemplate();

    public Map<String, Object> getNationality(String name){
        String url="https://api.nationalize.io?name={name}";
        return restTemplate.getForObject(url,Map.class, name);
    }
}
