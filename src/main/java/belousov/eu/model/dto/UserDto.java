package belousov.eu.model.dto;

public record UserDto(
        int id,
        String name,
        String email,
        String role,
        boolean active
) {
}
