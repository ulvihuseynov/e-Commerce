package org.ideaprojects.ecommerce.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long categoryId;

    @NotBlank(message = "Category name is not blank")
    @Size(min = 3, message = "Category name must bu 3 at least characters")
    private String categoryName;
}
