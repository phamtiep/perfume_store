package com.backend.java.perfume.service.Impl;

import com.backend.java.perfume.util.TestConstants;
import com.backend.java.perfume.enums.AuthProvider;
import com.backend.java.perfume.enums.Role;
import com.backend.java.perfume.domain.User;
import com.backend.java.perfume.repository.UserRepository;
import com.backend.java.perfume.security.JwtProvider;
import com.backend.java.perfume.security.oauth2.*;
import com.backend.java.perfume.service.email.MailSender;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AuthenticationServiceImplTest {

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private MailSender mailSender;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Value("${hostname}")
    private String hostname;

    @Test
    public void findByPasswordResetCode() {
        User user = new User();
        user.setPasswordResetCode(TestConstants.USER_PASSWORD_RESET_CODE);
        when(userRepository.getEmailByPasswordResetCode(TestConstants.USER_PASSWORD_RESET_CODE)).thenReturn(Optional.of(TestConstants.USER_EMAIL));
        authenticationService.getEmailByPasswordResetCode(TestConstants.USER_PASSWORD_RESET_CODE);

        assertEquals(TestConstants.USER_PASSWORD_RESET_CODE, user.getPasswordResetCode());
        verify(userRepository, times(1)).getEmailByPasswordResetCode(TestConstants.USER_PASSWORD_RESET_CODE);
    }

    @Test
    public void login() {
        User user = new User();
        user.setId(123L);
        user.setEmail(TestConstants.USER_EMAIL);
        user.setPassword(TestConstants.USER_PASSWORD);
        user.setActive(true);
        user.setFirstName(TestConstants.FIRST_NAME);
        user.setRoles(Collections.singleton(Role.USER));

        when(userRepository.findByEmail(TestConstants.USER_EMAIL)).thenReturn(Optional.of(user));
        assertEquals(123L, user.getId());
        assertEquals(TestConstants.USER_EMAIL, user.getEmail());
        assertEquals(TestConstants.FIRST_NAME, user.getFirstName());
        authenticationService.login(TestConstants.USER_EMAIL, TestConstants.USER_PASSWORD);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(jwtProvider, times(1)).createToken(user.getEmail(), user.getRoles().iterator().next().name());
    }

    @Test
    public void registerUser() {
        User user = new User();
        user.setFirstName(TestConstants.FIRST_NAME);
        user.setEmail(TestConstants.USER_EMAIL);
        String userCreated = authenticationService.registerUser(user, "", TestConstants.USER_PASSWORD);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("firstName", TestConstants.FIRST_NAME);
        attributes.put("registrationUrl", "http://" + hostname + "/activate/" + user.getActivationCode());

        assertNotNull(userCreated);
        assertNotNull(user.getActivationCode());
        assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.USER)));
        verify(userRepository, times(1)).save(user);
        verify(mailSender, times(1))
                .sendMessageHtml(
                        ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.eq("Activation code"),
                        ArgumentMatchers.eq("registration-template"),
                        ArgumentMatchers.eq(attributes));
    }

    @Test
    public void registerGoogleOauthUser() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", 123456);
        attributes.put("given_name", TestConstants.FIRST_NAME);
        attributes.put("family_name", TestConstants.LAST_NAME);
        attributes.put("email", TestConstants.USER_EMAIL);
        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(attributes);
        OAuth2UserInfo google = OAuth2UserFactory.getOAuth2UserInfo("google", attributes);

        User user = new User();
        user.setEmail(TestConstants.USER_EMAIL);
        user.setFirstName(TestConstants.FIRST_NAME);
        user.setLastName(TestConstants.LAST_NAME);
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setProvider(AuthProvider.GOOGLE);

        when(userRepository.save(user)).thenReturn(user);
        authenticationService.registerOauth2User("google", userInfo);
        assertEquals(TestConstants.USER_EMAIL, user.getEmail());
        assertEquals(TestConstants.FIRST_NAME, user.getFirstName());
        assertEquals(TestConstants.LAST_NAME, user.getLastName());
        assertEquals(google.getAttributes(), userInfo.getAttributes());
        assertNull(user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void registerFacebookOauthUser() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 123456);
        attributes.put("first_name", TestConstants.FIRST_NAME);
        attributes.put("last_name", TestConstants.LAST_NAME);
        attributes.put("email", TestConstants.USER_EMAIL);
        FacebookOAuth2UserInfo userInfo = new FacebookOAuth2UserInfo(attributes);
        OAuth2UserInfo facebook = OAuth2UserFactory.getOAuth2UserInfo("facebook", attributes);

        User user = new User();
        user.setEmail(TestConstants.USER_EMAIL);
        user.setFirstName(TestConstants.FIRST_NAME);
        user.setLastName(TestConstants.LAST_NAME);
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setProvider(AuthProvider.FACEBOOK);

        when(userRepository.save(user)).thenReturn(user);
        authenticationService.registerOauth2User("facebook", userInfo);
        assertEquals(TestConstants.USER_EMAIL, user.getEmail());
        assertEquals(TestConstants.FIRST_NAME, user.getFirstName());
        assertEquals(TestConstants.LAST_NAME, user.getLastName());
        assertEquals(facebook.getAttributes(), userInfo.getAttributes());
        assertNull(user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void registerGithubOauthUser() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 123456);
        attributes.put("name", TestConstants.FIRST_NAME);
        attributes.put("email", TestConstants.USER_EMAIL);
        GithubOAuth2UserInfo userInfo = new GithubOAuth2UserInfo(attributes);
        OAuth2UserInfo github = OAuth2UserFactory.getOAuth2UserInfo("github", attributes);

        User user = new User();
        user.setEmail(TestConstants.USER_EMAIL);
        user.setFirstName(TestConstants.FIRST_NAME);
        user.setLastName("");
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setProvider(AuthProvider.GITHUB);

        when(userRepository.save(user)).thenReturn(user);
        authenticationService.registerOauth2User("github", userInfo);
        assertEquals(TestConstants.USER_EMAIL, user.getEmail());
        assertEquals(TestConstants.FIRST_NAME, user.getFirstName());
        assertEquals(github.getAttributes(), userInfo.getAttributes());
        assertNull(user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void updateOauthUser() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", 123456);
        attributes.put("given_name", TestConstants.FIRST_NAME);
        attributes.put("family_name", TestConstants.LAST_NAME);
        attributes.put("email", TestConstants.USER_EMAIL);
        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(attributes);

        User user = new User();
        user.setEmail(TestConstants.USER_EMAIL);
        user.setFirstName(TestConstants.FIRST_NAME);
        user.setLastName(TestConstants.LAST_NAME);
        user.setProvider(AuthProvider.GOOGLE);

        when(userRepository.save(user)).thenReturn(user);
        authenticationService.updateOauth2User(user, "google", userInfo);
        assertEquals(TestConstants.USER_EMAIL, user.getEmail());
        assertEquals(TestConstants.FIRST_NAME, user.getFirstName());
        assertEquals(TestConstants.LAST_NAME, user.getLastName());
        assertEquals(AuthProvider.GOOGLE, user.getProvider());
        assertNull(user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void sendPasswordResetCode() {
        User user = new User();
        user.setEmail(TestConstants.USER_EMAIL);
        user.setPasswordResetCode(TestConstants.USER_PASSWORD_RESET_CODE);

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(TestConstants.USER_EMAIL)).thenReturn(Optional.of(user));
        authenticationService.sendPasswordResetCode(TestConstants.USER_EMAIL);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("firstName", null);
        attributes.put("resetUrl", "http://" + hostname + "/reset/" + user.getPasswordResetCode());

        assertEquals(TestConstants.USER_EMAIL, user.getEmail());
        assertNotNull(user.getPasswordResetCode());
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(mailSender, times(1))
                .sendMessageHtml(
                        ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.eq("Password reset"),
                        ArgumentMatchers.eq("password-reset-template"),
                        ArgumentMatchers.eq(attributes));
    }

    @Test
    public void passwordReset() {
        User user = new User();
        user.setEmail(TestConstants.USER_EMAIL);
        user.setPassword(TestConstants.USER_PASSWORD);

        when(userRepository.findByEmail(TestConstants.USER_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(TestConstants.USER_PASSWORD)).thenReturn(user.getPassword());
        when(userRepository.save(user)).thenReturn(user);
        authenticationService.passwordReset(user.getEmail(), user.getPassword(), user.getPassword());
        assertEquals(TestConstants.USER_EMAIL, user.getEmail());
        assertNotNull(user.getPassword());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(passwordEncoder, times(1)).encode(user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void activateUser() {
        User user = new User();
        user.setActivationCode(TestConstants.USER_ACTIVATION_CODE);

        when(userRepository.findByActivationCode(TestConstants.USER_ACTIVATION_CODE)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        String activated = authenticationService.activateUser(user.getActivationCode());
        assertNotNull(activated);
        assertNull(user.getActivationCode());
        verify(userRepository, times(1)).save(user);
    }
}
