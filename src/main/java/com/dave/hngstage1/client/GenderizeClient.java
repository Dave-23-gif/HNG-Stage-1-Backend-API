package com.dave.hngstage1.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service

public class GenderizeClient {
    private final RestTemplate restTemplate=new RestTemplate();
    public Map<String, Object> getGender(String name){
        String url="https://api.genderize.io?name={name}";

        return restTemplate.getForObject(url,Map.class, name);
    }
}
