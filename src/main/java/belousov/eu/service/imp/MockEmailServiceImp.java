package belousov.eu.service.imp;

import belousov.eu.service.EmailService;

/**
 * Класс для имитации отправки электронной почты.
 * В этом примере он просто выводит информацию об отправляемой почте в консоль.
 * Это может быть заменено на реальный код для отправки электронной почты.
 */
public class MockEmailServiceImp implements EmailService {

    /**
     * Имитация отправки письма.
     *
     * @param email   - адрес электронной почты
     * @param subject - тема письма
     * @param body    - тело письма
     */
    @Override
    public void sendEmail(String email, String subject, String body) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        System.out.println("Sending email to: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("Email sent successfully.");

    }
}
