package com.dave.hngstage1.parser;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class QueryParser {

    public Map<String, Object> parse(String query) {

        Map<String, Object> filters = new HashMap<>();

        String q = query.toLowerCase();

        // ====================================
        // GENDER
        // ====================================

        if (q.matches(".*\\bmale\\b.*") ||
                q.matches(".*\\bmales\\b.*")) {

            filters.put("gender", "male");
        }

        if (q.matches(".*\\bfemale\\b.*") ||
                q.matches(".*\\bfemales\\b.*")) {

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
        Pattern pattern = Pattern.compile("above\\s+(\\d+)");
        Matcher matcher = pattern.matcher(q);

        if (matcher.find()) {
            filters.put("minAge",
                    Integer.parseInt(matcher.group(1)));
        }
        if(filters.isEmpty()){
            throw new RuntimeException(
                    "Unable to interpret query"
            );
        }

        return filters;

    }

}