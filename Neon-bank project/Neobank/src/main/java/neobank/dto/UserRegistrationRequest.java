package neobank.dto;


import jakarta.validation.constraints.*;

public record UserRegistrationRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$") String phoneNumber
) {}