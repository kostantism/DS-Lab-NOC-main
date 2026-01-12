package gr.hua.dit.noc.core.impl;

import gr.hua.dit.noc.config.RouteeSmsProperties;
import gr.hua.dit.noc.core.EmailService;
import gr.hua.dit.noc.core.model.SendEmailRequest;
import gr.hua.dit.noc.core.model.SendEmailResult;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class RouteeEmailService implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteeEmailService.class);

    private static final String AUTHENTICATION_URL = "https://auth.routee.net/oauth/token";
    private static final String EMAIL_URL = "https://connect.routee.net/email/send";

    private final RestTemplate restTemplate;
    private final RouteeSmsProperties routeeProperties;

    public RouteeEmailService(final RestTemplate restTemplate, final RouteeSmsProperties routeeProperties) {
        if (restTemplate == null) throw new NullPointerException();
        if (routeeProperties == null) throw new NullPointerException();

        this.restTemplate = restTemplate;
        this.routeeProperties = routeeProperties;
    }


    @SuppressWarnings("rawtypes")
    @Cacheable("routeeAccessToken")
    public String getAccessToken() {
        LOGGER.info("Requesting Routee Access Token");

        final String credentials = routeeProperties.getAppId() + ":" + routeeProperties.getAppSecret();

        final String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Basic " + encoded);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", httpHeaders);

        final ResponseEntity<Map> response =
                this.restTemplate.exchange(AUTHENTICATION_URL, HttpMethod.POST, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

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

        final String token = this.getAccessToken();

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        final Map<String, Object> body = Map.of(
                "subject", subject,
                "body", content,
                "to", to,
                "from", this.routeeProperties.getSender());

        final HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        final ResponseEntity<String> response = this.restTemplate.postForEntity(EMAIL_URL, entity, String.class);

        LOGGER.info("Routee response: {}", response);

        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error("Email send to {} failed", to);
            return new SendEmailResult(false);
        }

        return new SendEmailResult(true);
    }
}
