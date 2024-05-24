package com.backend.java.perfume.security;

import com.backend.java.perfume.domain.User;
import com.backend.java.perfume.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.backend.java.perfume.constants.ErrorMessage.USER_NOT_FOUND;

@Service("userDetailsServiceImpl")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        if (user.getActivationCode() != null) {
            throw new LockedException("Email not activated");
        }
        return UserPrincipal.create(user);
    }
}
