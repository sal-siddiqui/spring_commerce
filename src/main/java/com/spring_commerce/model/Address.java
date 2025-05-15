package com.spring_commerce.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    // -- Auto-generated primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -- Required fields from request body
    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street can be at most 100 characters")
    private String street;

    @Size(max = 100, message = "Building name can be at most 100 characters")
    private String buildingName;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City can be at most 50 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State can be at most 50 characters")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country can be at most 50 characters")
    private String country;

    @NotBlank(message = "Pin code is required")
    @Pattern(regexp = "\\d{5,6}", message = "Pin code must be 5 or 6 digits")
    private String pinCode;

    // -- Relationships
    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(String street, String buildingName, String city, String state, String country, String pinCode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pinCode = pinCode;
    }

}
