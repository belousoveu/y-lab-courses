package belousov.eu.service;

import belousov.eu.model.dto.EmailDto;

public interface EmailService {
    void sendEmail(EmailDto emailDto);
}
