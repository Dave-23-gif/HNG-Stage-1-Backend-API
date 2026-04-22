package com.dave.hngstage1.parser;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class QueryParser {

    public Map<String, Object> parse(String query) {

        Map<String, Object> filters = new HashMap<>();

        String q = query.toLowerCase();

        // ====================================
        // GENDER
        // ====================================

        if (q.contains("male")) {
            filters.put("gender", "male");
        }

        if (q.contains("female")) {
            filters.put("gender", "female");
        }

        // ====================================
        // YOUNG
        // ====================================

        if (q.contains("young")) {
            filters.put("minAge", 16);
            filters.put("maxAge", 24);
        }

        // ====================================
        // AGE GROUPS
        // ====================================

        if (q.contains("child")) {
            filters.put("ageGroup", "child");
        }

        if (q.contains("teenager")) {
            filters.put("ageGroup", "teenager");
        }

        if (q.contains("adult")) {
            filters.put("ageGroup", "adult");
        }

        if (q.contains("senior")) {
            filters.put("ageGroup", "senior");
        }

        // ====================================
        // COUNTRIES
        // ====================================

        if (q.contains("nigeria")) {
            filters.put("countryId", "NG");
        }

        if (q.contains("kenya")) {
            filters.put("countryId", "KE");
        }

        if (q.contains("angola")) {
            filters.put("countryId", "AO");
        }

        return filters;

    }

}