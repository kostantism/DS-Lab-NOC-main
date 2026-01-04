package gr.hua.dit.noc.web.rest;

import gr.hua.dit.noc.core.EmailService;
import gr.hua.dit.noc.core.model.SendEmailRequest;
import gr.hua.dit.noc.core.model.SendEmailResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
public class EmailResource {

    private final EmailService emailService;

    public EmailResource(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public SendEmailResult send(@RequestBody SendEmailRequest request) {
        boolean sent = emailService.sendEmail(
                request.to(),
                request.subject(),
                request.body()
        );
        return new SendEmailResult(sent);
    }
}

