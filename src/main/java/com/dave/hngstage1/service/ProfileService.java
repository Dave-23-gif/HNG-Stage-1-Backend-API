package com.dave.hngstage1.service;

import com.dave.hngstage1.client.AgifyClient;
import com.dave.hngstage1.client.GenderizeClient;
import com.dave.hngstage1.client.NationalizeClient;
import com.dave.hngstage1.entity.Profile;
import com.dave.hngstage1.exception.ExternalApiException;
import com.dave.hngstage1.parser.QueryParser;
import com.dave.hngstage1.repository.ProfileRepository;
import com.dave.hngstage1.specification.ProfileSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final GenderizeClient genderizeClient;
    private final AgifyClient agifyClient;
    private final NationalizeClient nationalizeClient;
    private final QueryParser queryParser;

    // =========================================
    // CREATE PROFILE
    // =========================================

    public Profile createProfile(String name) {

        // CHECK DUPLICATES
        Optional<Profile> existingProfile =
                profileRepository.findByNameIgnoreCase(name);

        if (existingProfile.isPresent()) {
            return existingProfile.get();
        }

        // CALL EXTERNAL APIs
        Map<String, Object> genderData =
                genderizeClient.getGender(name);

        Map<String, Object> ageData =
                agifyClient.getAge(name);

        Map<String, Object> nationalityData =
                nationalizeClient.getNationality(name);

        // =========================================
        // GENDER DATA
        // =========================================

        String gender = (String) genderData.get("gender");

        Number probabilityRaw =
                (Number) genderData.get("probability");

        Number countRaw =
                (Number) genderData.get("count");

        if (gender == null ||
                countRaw == null ||
                countRaw.intValue() == 0) {

            throw new ExternalApiException(
                    "Genderize returned an invalid response"
            );
        }

        double genderProbability =
                probabilityRaw != null
                        ? probabilityRaw.doubleValue()
                        : 0.0;

        int sampleSize = countRaw.intValue();

        // =========================================
        // AGE DATA
        // =========================================

        if (ageData.get("age") == null) {

            throw new ExternalApiException(
                    "Agify returned an invalid response"
            );
        }

        int age =
                ((Number) ageData.get("age")).intValue();

        String ageGroup;

        if (age <= 12) {
            ageGroup = "child";
        } else if (age <= 19) {
            ageGroup = "teenager";
        } else if (age <= 59) {
            ageGroup = "adult";
        } else {
            ageGroup = "senior";
        }

        // =========================================
        // NATIONALITY DATA
        // =========================================

        List<Map<String, Object>> countries =
                (List<Map<String, Object>>) nationalityData.get("country");

        if (countries == null || countries.isEmpty()) {

            throw new ExternalApiException(
                    "Nationalize returned an invalid response"
            );
        }

        // PICK HIGHEST PROBABILITY COUNTRY
        Map<String, Object> topCountry =
                countries.stream()
                        .max((c1, c2) -> Double.compare(
                                ((Number) c1.get("probability")).doubleValue(),
                                ((Number) c2.get("probability")).doubleValue()
                        ))
                        .orElseThrow(() ->
                                new ExternalApiException(
                                        "Nationalize returned an invalid response"
                                ));

        String countryId =
                (String) topCountry.get("country_id");

        double countryProbability =
                ((Number) topCountry.get("probability")).doubleValue();

        // OPTIONAL COUNTRY NAME MAPPING
        String countryName = getCountryName(countryId);

        // =========================================
        // BUILD PROFILE
        // =========================================

        Profile profile = Profile.builder()
                .name(name)
                .gender(gender)
                .genderProbability(genderProbability)
                .sampleSize(sampleSize)
                .age(age)
                .ageGroup(ageGroup)
                .countryId(countryId)
                .countryName(countryName)
                .countryProbability(countryProbability)
                .build();

        // SAVE
        profileRepository.save(profile);

        return profile;
    }

    // =========================================
    // GET ALL PROFILES
    // =========================================

    public Page<Profile> getAllProfiles(

            String gender,
            String ageGroup,
            String countryId,
            Integer minAge,
            Integer maxAge,
            Double minGenderProbability,
            Double minCountryProbability,
            String sortBy,
            String order,
            int page,
            int limit

    ) {

        // DEFAULT SORT FIELD
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "createdAt";
        }

        // DEFAULT ORDER
        if (order == null || order.isBlank()) {
            order = "desc";
        }

        // SORTING
        Sort sort = order.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // PAGINATION
        Pageable pageable = PageRequest.of(
                page - 1,
                Math.min(limit, 50),
                sort
        );

        // DYNAMIC FILTERS
        Specification<Profile> spec = Specification
                .where(ProfileSpecification.hasGender(gender))
                .and(ProfileSpecification.hasAgeGroup(ageGroup))
                .and(ProfileSpecification.hasCountryId(countryId))
                .and(ProfileSpecification.hasMinAge(minAge))
                .and(ProfileSpecification.hasMaxAge(maxAge))
                .and(ProfileSpecification.hasMinGenderProbability(minGenderProbability))
                .and(ProfileSpecification.hasMinCountryProbability(minCountryProbability));

        // DATABASE QUERY
        return profileRepository.findAll(spec, pageable);
    }

    // =========================================
    // GET SINGLE PROFILE
    // =========================================

    public Optional<Profile> getProfileById(UUID id) {

        return profileRepository.findById(id);
    }

    // =========================================
    // DELETE PROFILE
    // =========================================

    public boolean deleteProfile(UUID id) {

        if (!profileRepository.existsById(id)) {
            return false;
        }

        profileRepository.deleteById(id);

        return true;
    }

    // =========================================
    // COUNTRY NAME HELPER
    // =========================================

    private String getCountryName(String countryId) {

        Map<String, String> countries = Map.ofEntries(
                Map.entry("NG", "Nigeria"),
                Map.entry("KE", "Kenya"),
                Map.entry("US", "United States"),
                Map.entry("GB", "United Kingdom"),
                Map.entry("IN", "India"),
                Map.entry("CA", "Canada"),
                Map.entry("ZA", "South Africa"),
                Map.entry("GH", "Ghana"),
                Map.entry("AO", "Angola"),
                Map.entry("BJ", "Benin"),
                Map.entry("TZ", "Tanzania"),
                Map.entry("UG", "Uganda")
        );

        return countries.getOrDefault(
                countryId,
                "Unknown"
        );
    }
    public Page<Profile> searchProfiles(
            String query,
            int page,
            int limit
    ) {

        Map<String, Object> filters = queryParser.parse(query);
        System.out.println(filters);

        return getAllProfiles(

                (String) filters.get("gender"),

                (String) filters.get("ageGroup"),

                (String) filters.get("countryId"),

                (Integer) filters.get("minAge"),

                (Integer) filters.get("maxAge"),

                null,

                null,

                "createdAt",

                "desc",

                page,

                limit
        );
    }
}