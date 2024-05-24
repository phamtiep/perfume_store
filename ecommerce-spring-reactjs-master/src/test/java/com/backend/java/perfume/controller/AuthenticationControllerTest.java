package com.backend.java.perfume.controller;

import com.backend.java.perfume.util.TestConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.java.perfume.dto.PasswordResetRequest;
import com.backend.java.perfume.dto.auth.AuthenticationRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.backend.java.perfume.constants.ErrorMessage.*;
import static com.backend.java.perfume.constants.PathConstants.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/sql/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private AuthenticationRequest authenticationRequest;
    private PasswordResetRequest passwordResetRequest;

    @Before
    public void init() {
        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail(TestConstants.USER_EMAIL);

        passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setEmail(TestConstants.USER_EMAIL);
        passwordResetRequest.setPassword(TestConstants.USER_PASSWORD);
        passwordResetRequest.setPassword2(TestConstants.USER_PASSWORD);
    }

    @Test
    public void login() throws Exception {
        authenticationRequest.setPassword(TestConstants.USER_PASSWORD);

        mockMvc.perform(post(API_V1_AUTH + LOGIN)
                        .content(mapper.writeValueAsString(authenticationRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void login_ShouldEmailOrPasswordBeNotValid() throws Exception {
        authenticationRequest.setPassword("123");

        mockMvc.perform(post(API_V1_AUTH + LOGIN)
                        .content(mapper.writeValueAsString(authenticationRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$", is(INCORRECT_PASSWORD)));
    }

    @Test
    public void forgotPassword() throws Exception {
        mockMvc.perform(get(API_V1_AUTH + FORGOT_EMAIL, TestConstants.USER_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Reset password code is send to your E-mail")));
    }

    @Test
    public void forgotPassword_ShouldEmailBeNotValid() throws Exception {
        mockMvc.perform(get(API_V1_AUTH + FORGOT_EMAIL, TestConstants.EMAIL_FAILURE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is(EMAIL_NOT_FOUND)));
    }

    @Test
    public void getPasswordResetCode() throws Exception {
        mockMvc.perform(get(API_V1_AUTH + RESET_CODE, TestConstants.USER_PASSWORD_RESET_CODE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(TestConstants.USER2_EMAIL));
    }

    @Test
    public void passwordReset() throws Exception {
        mockMvc.perform(post(API_V1_AUTH + RESET)
                        .content(mapper.writeValueAsString(passwordResetRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Password successfully changed!")));
    }

    @Test
    public void passwordReset_ShouldPasswordsNotMatch() throws Exception {
        passwordResetRequest.setPassword2("12345");

        mockMvc.perform(post(API_V1_AUTH + RESET)
                        .content(mapper.writeValueAsString(passwordResetRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.passwordError", is(PASSWORDS_DO_NOT_MATCH)));
    }

    @Test
    public void passwordReset_ShouldPassword2BeEmpty() throws Exception {
        passwordResetRequest.setPassword2("");

        mockMvc.perform(post(API_V1_AUTH + RESET)
                        .content(mapper.writeValueAsString(passwordResetRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password2Error", is(EMPTY_PASSWORD_CONFIRMATION)));
    }

    @Test
    @WithUserDetails(TestConstants.USER_EMAIL)
    public void updateUserPassword() throws Exception {
        PasswordResetRequest requestDto = new PasswordResetRequest();
        requestDto.setPassword(TestConstants.USER_PASSWORD);
        requestDto.setPassword2(TestConstants.USER_PASSWORD);

        mockMvc.perform(put(API_V1_AUTH + EDIT_PASSWORD)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Password successfully changed!")));
    }

    @Test
    @WithUserDetails(TestConstants.USER_EMAIL)
    public void updateUserPassword_ShouldPasswordsNotMatch() throws Exception {
        PasswordResetRequest requestDto = new PasswordResetRequest();
        requestDto.setPassword(TestConstants.USER_PASSWORD);
        requestDto.setPassword2("testpassword");

        mockMvc.perform(put(API_V1_AUTH + EDIT_PASSWORD)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.passwordError", is(PASSWORDS_DO_NOT_MATCH)));
    }

    @Test
    @WithUserDetails(TestConstants.USER_EMAIL)
    public void updateUserPassword_ShouldInputFieldsAreEmpty() throws Exception {
        PasswordResetRequest requestDto = new PasswordResetRequest();
        requestDto.setPassword("");
        requestDto.setPassword2("");

        mockMvc.perform(put(API_V1_AUTH + EDIT_PASSWORD)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.passwordError", is(PASSWORD_CHARACTER_LENGTH)))
                .andExpect(jsonPath("$.password2Error", is(PASSWORD2_CHARACTER_LENGTH)));
    }
}
