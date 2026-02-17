package neobank.dto;


import jakarta.validation.constraints.*;

public record UserLoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}