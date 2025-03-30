package belousov.eu.service.imp;

import belousov.eu.model.dto.EmailDto;
import belousov.eu.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Класс для имитации отправки электронной почты.
 * В этом примере он просто выводит информацию об отправляемой почте в консоль.
 * Это может быть заменено на реальный код для отправки электронной почты.
 */
@Service
@Slf4j
public class MockEmailServiceImp implements EmailService {

    /**
     * Имитация отправки письма.
     *
     * @param emailDto объект с данными для отправки электронной почты
     */
    @Override
    public void sendEmail(EmailDto emailDto) {
        if (emailDto.email() == null || emailDto.email().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        log.info("""
                Sending email to: {}
                subject: {}"
                body: {}
                sent successfully
                """, emailDto.email(), emailDto.subject(), emailDto.body());

    }
}
