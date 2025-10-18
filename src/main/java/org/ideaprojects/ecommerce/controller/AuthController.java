package org.ideaprojects.ecommerce.controller;

import jakarta.validation.Valid;
import org.ideaprojects.ecommerce.model.AppRole;
import org.ideaprojects.ecommerce.model.Role;
import org.ideaprojects.ecommerce.model.User;
import org.ideaprojects.ecommerce.repository.RoleRepository;
import org.ideaprojects.ecommerce.repository.UserRepository;
import org.ideaprojects.ecommerce.security.jwt.JWTUtils;
import org.ideaprojects.ecommerce.security.request.LoginRequest;
import org.ideaprojects.ecommerce.security.request.SignUpRequest;
import org.ideaprojects.ecommerce.security.response.MessageResponse;
import org.ideaprojects.ecommerce.security.response.UserInfoResponse;
import org.ideaprojects.ecommerce.security.services.UserDetailsImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthController(AuthenticationManager authenticationManager, JWTUtils jwtUtils, UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){

        Authentication authentication;

        try{
            authentication=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserName(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            Map<String,Object> map=new HashMap<>();
            map.put("message","Bad credentials");
            map.put("status",false);

            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        UserInfoResponse userInfoResponse =new UserInfoResponse(userDetails.getId(),jwtCookie.toString(),userDetails.getUsername(),roles);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                jwtCookie.toString())
                .body(userInfoResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){

        if (userRepository.existsByUserName(signUpRequest.getUserName())){

            return ResponseEntity.badRequest().
                    body(new MessageResponse("Error: User name is already taken"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())){

            return ResponseEntity.badRequest().
                    body(new MessageResponse("Error: Email is already taken"));
        }

        User user=new User(
                signUpRequest.getUserName(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );
        Set<String> strRole = signUpRequest.getRole();

        Set<Role> roles=new HashSet<>();

        if (strRole==null){
            Role userRole=roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(()->new RuntimeException("Error: role not found"));
            roles.add(userRole);
        }else{
            strRole.forEach(role -> {
                switch (role){
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: role not found"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: role not found"));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole=roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(()->new RuntimeException("Error: role not found"));
                        roles.add(userRole);
                }
            });

        }
        user.setRoles(roles);
        userRepository.save(user);

        return  ResponseEntity.ok(new MessageResponse("User register successfully"));
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication){

        if (authentication != null) {
            return authentication.getName();
        }else{
            return "";
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        UserInfoResponse userInfoResponse =new UserInfoResponse(userDetails.getId(),userDetails.getUsername(),roles);

        return ResponseEntity.ok().body(userInfoResponse);

    }
}
