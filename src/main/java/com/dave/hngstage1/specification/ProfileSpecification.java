package com.dave.hngstage1.specification;

import com.dave.hngstage1.entity.Profile;
import org.springframework.data.jpa.domain.Specification;

public class ProfileSpecification {
    public static Specification<Profile> hasGender(String gender) {
        return(root, query, criteriaBuilder) ->{
            //if no gender is provided, return all records
            if(gender==null || gender.isBlank()){
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("gender")),
                    gender.toLowerCase()
            );
        };

    }
    public static Specification<Profile> hasCountryId(String countryId) {
        return(root, query, criteriaBuilder) ->{
            //if no country is provided, return all records
            if(countryId==null || countryId.isBlank()){
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("countryId")),
                    countryId.toLowerCase()
            );
        };

    }
    public static Specification<Profile> hasAgeGroup(String ageGroup) {
        return(root, query, criteriaBuilder) ->{
            //if no age is provided, return all records
            if(ageGroup==null || ageGroup.isBlank()){
                return criteriaBuilder.conjunction();
            }
            //WHERE LOWER(age) = LOWER(?)
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("ageGroup")),
                    ageGroup.toLowerCase()
            );
        };

    }
    public static Specification<Profile> hasMinAge(Integer minAge) {

        return (root, query, criteriaBuilder) -> {

            // If no minimum age provided, return all
            if (minAge == null) {
                return criteriaBuilder.conjunction();
            }

            // WHERE age >= ?
            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("age"),
                    minAge
            );
        };
    }
    public static Specification<Profile> hasMaxAge(Integer maxnAge) {

        return (root, query, criteriaBuilder) -> {

            // If no max age provided, return all
            if (maxnAge == null) {
                return criteriaBuilder.conjunction();
            }

            // WHERE age >= ?
            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("age"),
                    maxnAge
            );
        };
    }
    public static Specification<Profile> hasMinGenderProbability(Double probability) {

        return (root, query, criteriaBuilder) -> {

            if (probability == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("genderProbability"),
                    probability
            );
        };
    }
    public static Specification<Profile> hasMinCountryProbability(Double probability) {

        return (root, query, criteriaBuilder) -> {

            if (probability == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("countryProbability"),
                    probability
            );
        };
    }
}
