package com.backend.java.perfume.mapper;

import com.backend.java.perfume.domain.User;
import com.backend.java.perfume.dto.PasswordResetRequest;
import com.backend.java.perfume.dto.RegistrationRequest;
import com.backend.java.perfume.dto.auth.AuthenticationRequest;
import com.backend.java.perfume.dto.auth.AuthenticationResponse;
import com.backend.java.perfume.dto.user.UserResponse;
import com.backend.java.perfume.exception.InputFieldException;
import com.backend.java.perfume.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthenticationMapper {

    private final AuthenticationService authenticationService;
    private final CommonMapper commonMapper;

    public AuthenticationResponse login(AuthenticationRequest request) {
        Map<String, Object> credentials = authenticationService.login(request.getEmail(), request.getPassword());
        AuthenticationResponse response = new AuthenticationResponse();
        response.setUser(commonMapper.convertToResponse(credentials.get("user"), UserResponse.class));
        response.setToken((String) credentials.get("token"));
        return response;
    }

    public String getEmailByPasswordResetCode(String code) {
        return authenticationService.getEmailByPasswordResetCode(code);
    }

    public String registerUser(String captcha, RegistrationRequest registrationRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
        User user = commonMapper.convertToEntity(registrationRequest, User.class);
        return authenticationService.registerUser(user, captcha, registrationRequest.getPassword2());
    }

    public String activateUser(String code) {
        return authenticationService.activateUser(code);
    }

    public String sendPasswordResetCode(String email) {
        return authenticationService.sendPasswordResetCode(email);
    }

    public String passwordReset(String email, PasswordResetRequest passwordReset) {
        return authenticationService.passwordReset(email, passwordReset.getPassword(), passwordReset.getPassword2());
    }

    public String passwordReset(String email, PasswordResetRequest passwordReset, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        } else {
            return authenticationService.passwordReset(email, passwordReset.getPassword(), passwordReset.getPassword2());
        }
    }
}
