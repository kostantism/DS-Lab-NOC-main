package gr.hua.dit.noc.web.rest;

import gr.hua.dit.noc.core.EmailService;
import gr.hua.dit.noc.core.model.SendEmailRequest;
import gr.hua.dit.noc.core.model.SendEmailResult;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/email" , produces = MediaType.APPLICATION_JSON_VALUE)
public class EmailResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailResource.class);

    private final EmailService emailService;

    public EmailResource(EmailService emailService) {
        if(emailService == null) throw new IllegalArgumentException();
        this.emailService = emailService;
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SendEmailResult> sendEmail(@RequestBody @Valid SendEmailRequest  sendEmailRequest) {
        final SendEmailResult sendEmailResult = emailService.send(sendEmailRequest);
        return ResponseEntity.ok(sendEmailResult);
    }
}

