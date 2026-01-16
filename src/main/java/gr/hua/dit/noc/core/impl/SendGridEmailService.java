package gr.hua.dit.noc.core.impl;


import gr.hua.dit.noc.config.EmailProperties;
import gr.hua.dit.noc.core.EmailService;
import gr.hua.dit.noc.core.model.SendEmailRequest;
import gr.hua.dit.noc.core.model.SendEmailResult;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * SendGrid implementation of {@link EmailService}.
 *
 * Secured external REST API call using Bearer token authentication.
 */
@Service
public class SendGridEmailService implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendGridEmailService.class);

    private static final String SENDGRID_URL = "https://api.sendgrid.com/v3/mail/send";

    private final RestTemplate restTemplate;
    private final EmailProperties emailProperties;

    public SendGridEmailService(final RestTemplate restTemplate,
                                final EmailProperties emailProperties) {
        if (restTemplate == null) throw new NullPointerException();
        if (emailProperties == null) throw new NullPointerException();

        this.restTemplate = restTemplate;
        this.emailProperties = emailProperties;
    }

    @Override
    public SendEmailResult send(@Valid final SendEmailRequest sendEmailRequest) {
        if (sendEmailRequest == null) throw new NullPointerException();

        // --------------------------------------------------
        // Extract request data
        // --------------------------------------------------

        final String to = sendEmailRequest.to();
        final String subject = sendEmailRequest.subject();
        final String body = sendEmailRequest.content();
        final String from = emailProperties.getSendgrid().getFrom();

        // --------------------------------------------------
        // Validation
        // --------------------------------------------------

        if (to == null || to.isBlank()) throw new IllegalArgumentException();
        if (subject == null || subject.isBlank()) throw new IllegalArgumentException();
        if (body == null || body.isBlank()) throw new IllegalArgumentException();
        if (from == null || from.isBlank()) throw new IllegalStateException("SendGrid 'from' address not configured");

        final String apiKey = emailProperties.getSendgrid().getApiKey();
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalStateException("SendGrid API key not configured");

        // --------------------------------------------------
        // Headers
        // --------------------------------------------------

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // --------------------------------------------------
        // Payload (SendGrid format)
        // --------------------------------------------------

        final Map<String, Object> payload = Map.of(
                "personalizations", new Object[]{
                        Map.of(
                                "to", new Object[]{
                                        Map.of("email", to)
                                },
                                "subject", subject
                        )
                },
                "from", Map.of("email", from),
                "content", new Object[]{
                        Map.of(
                                "type", "text/plain",
                                "value", body
                        )
                }
        );

        // --------------------------------------------------
        // Request
        // --------------------------------------------------

        final HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(payload, headers);

        try {
            final ResponseEntity<Void> response =
                    this.restTemplate.postForEntity(SENDGRID_URL, request, Void.class);

            LOGGER.info("SendGrid response status: {}", response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                LOGGER.error("Email send to {} failed via SendGrid", to);
                return new SendEmailResult(false);
            }

            return new SendEmailResult(true);

        } catch (Exception ex) {
            LOGGER.error("SendGrid email send failed", ex);
            return new SendEmailResult(false);
        }
    }
}

