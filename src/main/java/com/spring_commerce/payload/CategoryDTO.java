package com.spring_commerce.payload;

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
    private Long categoryId;

    @NotBlank
    private String categoryName;
}
