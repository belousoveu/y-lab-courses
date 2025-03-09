package belousov.eu.service;

public class EmailServiceImp implements EmailService {


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
