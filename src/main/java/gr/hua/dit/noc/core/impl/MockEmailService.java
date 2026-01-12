package gr.hua.dit.noc.core.impl;

import gr.hua.dit.noc.core.EmailService;
import gr.hua.dit.noc.core.SmsService;
import gr.hua.dit.noc.core.model.SendEmailRequest;
import gr.hua.dit.noc.core.model.SendEmailResult;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Mock {@link EmailService}.
 */
@Service
public class MockEmailService implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockEmailService.class);



    @Override
    public SendEmailResult send(@Valid final SendEmailRequest sendEmailRequest) {
        if (sendEmailRequest == null) throw new NullPointerException();

        final String to = sendEmailRequest.to();
        final String subject = sendEmailRequest.subject();
        final String content = sendEmailRequest.content();

        if (to == null) throw new NullPointerException();
        if (to.isBlank()) throw new IllegalArgumentException();
        if (subject == null) throw new NullPointerException();
        if (subject.isBlank()) throw new IllegalArgumentException();
        if (content == null) throw new NullPointerException();
        if (content.isBlank()) throw new IllegalArgumentException();

        LOGGER.info("SENDING EMAIL to={} subject={} content={}", to, subject, content);

        return new SendEmailResult(false);
    }
}

