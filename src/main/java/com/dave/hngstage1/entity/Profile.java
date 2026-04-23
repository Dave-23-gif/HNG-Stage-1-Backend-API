package com.dave.hngstage1.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "profiles")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Profile {
    @Id
    private UUID id;
    @Column(unique = true)
    private String name;
    @Column(name = "gender")
    private String gender;
    @JsonProperty("gender_probability")
    @Column(name = "gender_probability")
    private Double genderProbability;
    @JsonProperty("sample_size")
    @Column(name = "sample_size")
    private Integer sampleSize;
    @Column(name = "age")
    private Integer age;
    @JsonProperty("age-group")
    @Column(name = "age_group")
    private String ageGroup;
    @JsonProperty("country_id")
    @Column(name = "country_id")
    private String countryId;
    @JsonProperty("country_name")
    @Column(name = "country_name")
    private String countryName;
    @JsonProperty("country_probability")
    @Column(name = "country_probability")
    private Double countryProbability;
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
       if(this.createdAt == null) {
           this.createdAt = Instant.now();
       }

    }



}




