package root.service;

import root.dto.BillDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine; // tạo @Bean: Thymeleaf

    @Value("${upload.folder}")
    private String UPLOAD_FOLDER;

    public void sendBirthdayEmail(String to, String name) {
        String subject = "Happy Birthday ! " + name;

        Context context = new Context();
        context.setVariable("name", name); // truyen bien ${name} sang email/happy-birthday.html

        String body = templateEngine.process("email/happy-birthday.html", context);

        sendMail(to, subject, body);
    }

    public void sendBillCreateEmail(String to, String name, BillDTO billCreated) {
        String subject = "New Bill #" + billCreated.getId() + " | Project3 Springboot";

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("billCreated", billCreated);

        String body = templateEngine.process("email/bill/new-bill.html", context);

        sendMail(to, subject, body);
    }

    public void sendBillCreatePdfEmail(String to, BillDTO billCreated) {
        String subject = "New Bill #" + billCreated.getId() + " | Project3 Springboot";

        /* html body email begin */
        Context context = new Context();
        context.setVariable("name", billCreated.getUser().getName());
        context.setVariable("billCreated", billCreated);

        String body = templateEngine.process("email/bill/new-bill.html", context);

        body += "<p>Your bill information is in the attached pdf file</p>";
        /* html body email end */

        String filename = UPLOAD_FOLDER + "bill-id-" + billCreated.getId() + ".pdf";

        try {
            sendMailWithAttachment(to, subject, body, filename);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMail(String to, String subject, String body) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMailWithAttachment(String to, String subject, String body, String filePath) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
