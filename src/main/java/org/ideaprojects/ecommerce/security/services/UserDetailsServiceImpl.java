package org.ideaprojects.ecommerce.security.services;

import jakarta.transaction.Transactional;
import org.ideaprojects.ecommerce.model.User;
import org.ideaprojects.ecommerce.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user= userRepository.findByUserName(username)
               .orElseThrow(()->new UsernameNotFoundException("User not found with username: " + username));
        return UserDetailsImpl.build(user);
    }
}
