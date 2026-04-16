package com.dave.hngstage1.entity;

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
    @Column(name = "gender_probability")
    private Double genderProbability;
    @Column(name = "sample_size")
    private Integer sampleSize;
    @Column(name = "age")
    private Integer age;
    @Column(name = "age_group")
    private String ageGroup;
    @Column(name = "country_id")
    private String countryId;
    @Column(name = "country_probability")
    private Double countryProbability;
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




