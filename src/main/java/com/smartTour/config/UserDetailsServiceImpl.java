package com.smartTour.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smartTour.model.UserDtls;
import com.smartTour.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDtls user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("user not found with username:" + username);

        }
        if (!user.getIsEnable()) {
            throw new DisabledException("User account is not enabled");
        }

        return new CustomUser(user);
    }

}