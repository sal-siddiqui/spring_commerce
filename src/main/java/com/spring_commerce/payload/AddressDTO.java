package com.spring_commerce.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    // -- Auto-generated field
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
}
