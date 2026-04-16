package com.dave.hngstage1.controller;

import com.dave.hngstage1.entity.Profile;
import com.dave.hngstage1.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody Map<String, String> request){
        String name=request.get("name");
        if(name==null ||  name.trim().isEmpty()){
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", "Name parameter is required")
            );
        }
        Profile profile=profileService.createProfile(name);
        return ResponseEntity.status(201).body(
                Map.of("status", "success", "data", profile)
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable UUID id){
        Optional<Profile> profile = profileService.getProfileById(id);

        if(profile.isEmpty()){
            return ResponseEntity.status(404).body(
                    Map.of("status", "error", "message", "Profile  not found")

            );

        }
        return ResponseEntity.ok(
                Map.of("status", "success", "data", profile.get())
        );

    }
    @GetMapping
    public ResponseEntity<?> getProfiles(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String country_id,
            @RequestParam(required = false) String age_group
    ){
        List<Profile> profiles =profileService.getAllProfiles(gender, country_id, age_group);
        return ResponseEntity.ok(
                Map.of("status", "success", "count", profiles.size(), "data", profiles)
        );

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable UUID id){
        if(!profileService.deleteProfile(id)) {
            return ResponseEntity.status(404).body(
                    Map.of("status", "error", "message", "Profile not found")
            );
        }
        return ResponseEntity.noContent().build();
    }

}
