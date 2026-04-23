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
        q = " " + q + " ";

        // GENDER

        boolean hasMale =
                q.contains(" male") ||
                        q.startsWith("male") ||
                        q.contains(" males");

        boolean hasFemale =
                q.contains(" female") ||
                        q.startsWith("female") ||
                        q.contains(" females");

        if (hasMale && !hasFemale) {
            filters.put("gender", "male");
        }

        if (hasFemale && !hasMale) {
            filters.put("gender", "female");
        }

        // AGE GROUPS

        if (q.contains("teenager") ||
                q.contains("teenagers")) {

            filters.put("ageGroup", "teenager");
        }

        if (q.contains("adult") ||
                q.contains("adults")) {

            filters.put("ageGroup", "adult");
        }

        if (q.contains("senior") ||
                q.contains("seniors")) {

            filters.put("ageGroup", "senior");
        }

        if (q.contains("child") ||
                q.contains("children")) {

            filters.put("ageGroup", "child");
        }

        // YOUNG = 16-24

        if (q.contains("young")) {

            filters.put("minAge", 16);
            filters.put("maxAge", 24);
        }

        // ABOVE AGE

        Pattern pattern =
                Pattern.compile("above\\s+(\\d+)");

        Matcher matcher = pattern.matcher(q);

        if (matcher.find()) {

            filters.put(
                    "minAge",
                    Integer.parseInt(matcher.group(1))
            );
        }

        // COUNTRIES

        if (q.contains("kenya")) {
            filters.put("countryId", "KE");
        }

        if (q.contains("nigeria")) {
            filters.put("countryId", "NG");
        }

        if (q.contains("uganda")) {
            filters.put("countryId", "UG");
        }

        if (q.contains("tanzania")) {
            filters.put("countryId", "TZ");
        }

        if (q.contains("angola")) {
            filters.put("countryId", "AO");
        }

        // INVALID QUERY

        if (filters.isEmpty()) {

            throw new RuntimeException(
                    "Unable to interpret query"
            );
        }

        return filters;
    }
}