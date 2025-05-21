package com.spring_commerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String street;

    private String buildingName;

    private String city;

    private String state;

    private String country;

    private String pinCode;

    // -- Relationships
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(final String street, final String buildingName,
            final String city, final String state, final String country,
            final String pinCode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pinCode = pinCode;
    }

}
