package com.spring_commerce.payload;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @Null(message = "ID must be null during creation.")
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
}
