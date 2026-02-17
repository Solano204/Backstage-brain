package neobank.dto;


public record AuthResponse(
        String token,
        String type,
        Long userId,
        String email,
        String firstName,
        String lastName
) {
    public AuthResponse(String token, Long userId, String email, String firstName, String lastName) {
        this(token, "Bearer", userId, email, firstName, lastName);
    }
}