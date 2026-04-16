package com.dave.hngstage1.service;

import com.dave.hngstage1.client.AgifyClient;
import com.dave.hngstage1.client.GenderizeClient;
import com.dave.hngstage1.client.NationalizeClient;
import com.dave.hngstage1.entity.Profile;
import com.dave.hngstage1.exception.ExternalApiException;
import com.dave.hngstage1.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final GenderizeClient  genderizeClient;
    private final AgifyClient agifyClient;
    private final NationalizeClient nationalizeClient;


    public Profile createProfile(String name) {
        Optional<Profile> existingProfile=profileRepository.findByNameIgnoreCase(name);
        if(existingProfile.isPresent()){
            return existingProfile.get();
        }
        Map<String,Object> genderData=genderizeClient.getGender(name);
        Map<String,Object> ageData=agifyClient.getAge(name);
        Map<String,Object> nationalityData=nationalizeClient.getNationality(name);
        String gender=(String) genderData.get("gender");
        Double probability=((Number)genderData.get("probability")).doubleValue();
        Integer count=((Number)genderData.get("count")).intValue();
        if (ageData.get("age") == null) {
            throw new ExternalApiException("Agify returned an invalid response");
        }

        Integer age = ((Number) ageData.get("age")).intValue();
        



        if (gender==null || count==0) {
            throw new ExternalApiException("Genderize returned an invalid response");
        }
        if (ageData.get("age")==null) {
            throw new ExternalApiException("Agify returned an invalid response");
        }
        var countries = (List<Map<String, Object>>) nationalityData.get("country");

        if (countries == null || countries.isEmpty()) {
            throw new ExternalApiException("Nationalize returned an invalid response");
        }
        String ageGroup;
        if(age<=12) ageGroup="child";
        else if(age<=19) ageGroup="teenager";
        else if(age<=59) ageGroup ="adult";
        else ageGroup="senior";
        Map<String, Object> topCountry=countries.get(0);
        String countryId=(String) topCountry.get("country_id");
        double countryProbability=((Number)topCountry.get("probability")).doubleValue();

        Profile profile=Profile.builder()
                .name(name)
                .gender(gender)
                .genderProbability(probability)
                .sampleSize(count)
                .age(age)
                .ageGroup(ageGroup)
                .countryId(countryId)
                .countryProbability(countryProbability)
                .build();
        profileRepository.save(profile);

        return profile;

    }

    public Optional<Profile> getProfileById(UUID id) {
        return profileRepository.findById(id);
    }

    public List<Profile> getAllProfiles(String gender, String countryId, String ageGroup) {
        List<Profile> profiles =profileRepository.findAll();
        return profiles.stream()
                .filter(p-> gender==null || p.getGender().equalsIgnoreCase(gender))
                .filter(p-> countryId==null || p.getCountryId().equalsIgnoreCase(countryId))
                .filter(p-> ageGroup==null || p.getAgeGroup().equalsIgnoreCase(ageGroup))
                .toList();
    }

    public boolean deleteProfile(UUID id) {
        if(!profileRepository.existsById(id)){
            return false;
        }
        profileRepository.deleteById(id);
        return true;
    }
}
