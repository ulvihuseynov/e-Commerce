package org.ideaprojects.ecommerce.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.ideaprojects.ecommerce.model.Role;

import java.util.Set;

@Data
public class SignUpRequest {

    @NotBlank
    @Size(min=3,max = 20,message = "User name must be characters at least 3 and max 20")
    private String userName;

    @NotBlank
    @Size(min=3,max = 40,message = "User email must be characters at least 3 and max 40")
    @Email
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min=3,max = 40,message = "User password must be characters at least 3 and max 40")
    private String password;
}
