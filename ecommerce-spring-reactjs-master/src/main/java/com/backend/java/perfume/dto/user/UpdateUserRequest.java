package com.backend.java.perfume.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

import static com.backend.java.perfume.constants.ErrorMessage.EMPTY_FIRST_NAME;
import static com.backend.java.perfume.constants.ErrorMessage.EMPTY_LAST_NAME;

@Data
public class UpdateUserRequest {
    private Long id;

    @NotBlank(message = EMPTY_FIRST_NAME)
    private String firstName;

    @NotBlank(message = EMPTY_LAST_NAME)
    private String lastName;

    private String city;
    private String address;
    private String phoneNumber;
    private String postIndex;
}
