package org.ideaprojects.ecommerce.payload;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5,message = "Street name must be at least 5 characters")
    private String street;

    @NotBlank
    @Size(min = 5,message = "Building  name must be at least 5 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 3,message = "City name must be at least 3 characters")
    private String city;

    @NotBlank
    @Size(min = 2,message = "State name must be at least 2 characters")
    private String state;

    @NotBlank
    @Size(min = 3,message = "Country name must be at least 3 characters")
    private String country;

    @NotBlank
    @Size(min = 6,message = "Pin code must be at least 6 characters")
    private String pinCode;
}
