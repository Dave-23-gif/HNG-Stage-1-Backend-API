package com.dave.hngstage1.controller;

import com.dave.hngstage1.entity.Profile;
import com.dave.hngstage1.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    // =====================================
    // CREATE PROFILE
    // =====================================

    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody Map<String, String> request) {

        String name = request.get("name");

        if (name == null || name.trim().isEmpty()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "error",
                            "message", "Name is required"
                    )
            );
        }

        Profile profile = profileService.createProfile(name);

        return ResponseEntity.status(201).body(
                Map.of(
                        "status", "success",
                        "data", profile
                )
        );
    }

    // =====================================
    // GET ALL PROFILES
    // =====================================

    @GetMapping
    public ResponseEntity<?> getAllProfiles(

            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String age_group,
            @RequestParam(required = false) String country_id,
            @RequestParam(required = false) Integer min_age,
            @RequestParam(required = false) Integer max_age,
            @RequestParam(required = false) Double min_gender_probability,
            @RequestParam(required = false) Double min_country_probability,
            @RequestParam(defaultValue = "createdAt") String sort_by,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit

    ) {

        Page<Profile> profiles = profileService.getAllProfiles(
                gender,
                age_group,
                country_id,
                min_age,
                max_age,
                min_gender_probability,
                min_country_probability,
                sort_by,
                order,
                page,
                limit
        );

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "page", page,
                        "limit", limit,
                        "total", profiles.getTotalElements(),
                        "data", profiles.getContent()
                )
        );
    }

    // =====================================
    // GET SINGLE PROFILE
    // =====================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable UUID id) {

        return profileService.getProfileById(id)
                .map(profile -> ResponseEntity.ok(
                        Map.of(
                                "status", "success",
                                "data", profile
                        )
                ))
                .orElseGet(() -> ResponseEntity.status(404).body(
                        Map.of(
                                "status", "error",
                                "message", "Profile not found"
                        )
                ));
    }

    // =====================================
    // DELETE PROFILE
    // =====================================
    @GetMapping("/search")
    public ResponseEntity<?> searchProfiles(

            @RequestParam String q,

            @RequestParam(defaultValue = "1") int page,

            @RequestParam(defaultValue = "10") int limit

    ) {

        if (q == null || q.trim().isEmpty()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "error",
                            "message", "Query is required"
                    )
            );
        }

        Page<Profile> profiles =
                profileService.searchProfiles(
                        q,
                        page,
                        limit
                );

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "page", page,
                        "limit", limit,
                        "total", profiles.getTotalElements(),
                        "data", profiles.getContent()
                )
        );
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable UUID id) {

        boolean deleted = profileService.deleteProfile(id);

        if (!deleted) {

            return ResponseEntity.status(404).body(
                    Map.of(
                            "status", "error",
                            "message", "Profile not found"
                    )
            );
        }

        return ResponseEntity.noContent().build();
    }
}