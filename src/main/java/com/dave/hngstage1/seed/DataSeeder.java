package com.dave.hngstage1.seed;

import com.dave.hngstage1.entity.Profile;
import com.dave.hngstage1.repository.ProfileRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProfileRepository profileRepository;

    public DataSeeder(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {

        System.out.println("SEEDER STARTED");

        // prevent duplicate seeding
        if (profileRepository.count() > 0) {
            System.out.println("DATABASE ALREADY SEEDED");
            return;
        }

        InputStream inputStream =
                new ClassPathResource(
                        "seed_profiles.json"
                ).getInputStream();

        System.out.println("FILE LOADED");

        Map<String, Object> jsonData =
                objectMapper.readValue(
                        inputStream,
                        new TypeReference<>() {}
                );

        List<Map<String, Object>> profiles =
                (List<Map<String, Object>>)
                        jsonData.get("profiles");

        System.out.println(
                "TOTAL RECORDS = " + profiles.size()
        );

        for (Map<String, Object> data : profiles) {

            String name =
                    (String) data.get("name");

            // skip duplicates
            if (profileRepository
                    .findByNameIgnoreCase(name)
                    .isPresent()) {

                continue;
            }

            Integer age =
                    ((Number) data.get("age"))
                            .intValue();

            String ageGroup;

            if (age <= 12) {
                ageGroup = "child";
            }

            else if (age <= 19) {
                ageGroup = "teenager";
            }

            else if (age <= 59) {
                ageGroup = "adult";
            }

            else {
                ageGroup = "senior";
            }

            Profile profile = Profile.builder()

                    .name(name)

                    .gender(
                            (String) data.get("gender")
                    )

                    .genderProbability(
                            data.get("gender_probability") == null
                                    ? 0.0
                                    : ((Number) data.get(
                                    "gender_probability"
                            )).doubleValue()
                    )
                    .sampleSize(
                            data.get("sample_size") == null
                                    ? 0
                                    : ((Number) data.get(
                                    "sample_size"
                            )).intValue()
                    )


                    .age(age)

                    .ageGroup(ageGroup)

                    .countryId(
                            (String) data.get("country_id")
                    )

                    .countryName(
                            (String) data.get("country_name")
                    )

                    .countryProbability(
                            data.get("country_probability") == null
                                    ? 0.0
                                    : ((Number) data.get(
                                    "country_probability"
                            )).doubleValue()
                    )

                    .build();

            profileRepository.save(profile);
        }

        System.out.println("SEEDING COMPLETE");
    }
}