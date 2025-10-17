package org.ideaprojects.ecommerce.security.jwt;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponse {

    private String jwtToken;
    private String userName;
    private List<String> roles;

    public LoginResponse(String jwtToken, String userName, List<String> roles) {
        this.jwtToken = jwtToken;
        this.userName = userName;
        this.roles = roles;
    }

}
