package belousov.eu.model.dto;


import lombok.Getter;

@Getter
public record EmailDto(String email, String subject, String body) {
}
