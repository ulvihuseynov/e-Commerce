package org.ideaprojects.ecommerce.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long productId;

    @NotBlank(message = "Product name is not blank")
    @Size(min = 3, message = "Product name must bu 3 at least characters")
    private String productName;

    private String image;

    @NotBlank(message = "Product description  name is not blank")
    @Size(min = 6, message = "Product description must bu 6 at least characters")
    private String description;

    @Min(value = 0, message = "Product quantity at least 0")
    private Integer quantity;

    private double price;

    private double discount;

    private double specialPrice;
}
