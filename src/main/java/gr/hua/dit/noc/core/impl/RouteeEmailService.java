package gr.hua.dit.noc.core.impl;

import gr.hua.dit.noc.config.RouteeProperties;
import gr.hua.dit.noc.core.EmailService;
import gr.hua.dit.noc.core.model.SendEmailRequest;
import gr.hua.dit.noc.core.model.SendEmailResult;
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
    private static final String EMAIL_URL = "https://connect.routee.net/email";

    private final RestTemplate restTemplate;
    private final RouteeProperties routeeProperties;

    public RouteeEmailService(RestTemplate restTemplate,
                              RouteeProperties routeeProperties) {
        this.restTemplate = restTemplate;
        this.routeeProperties = routeeProperties;
    }

    // 🔐 ίδιο ακριβώς με SmsService
    @SuppressWarnings("rawtypes")
    @Cacheable("routeeAccessToken")
    public String getAccessToken() {
        LOGGER.info("Requesting Routee Access Token (EMAIL)");

        final String credentials =
                routeeProperties.getAppId() + ":" + routeeProperties.getAppSecret();

        final String encoded =
                Base64.getEncoder()
                        .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encoded);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final HttpEntity<String> request =
                new HttpEntity<>("grant_type=client_credentials", headers);

        final ResponseEntity<Map> response =
                restTemplate.exchange(
                        AUTHENTICATION_URL,
                        HttpMethod.POST,
                        request,
                        Map.class
                );

        return (String) response.getBody().get("access_token");
    }

    @Override
    public boolean sendEmail(String to, String subject, String content) {

        final String token = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        SendEmailRequest body = new SendEmailRequest(
                to,
                subject,
                content,
                routeeProperties.getSender()
        );

        HttpEntity<SendEmailRequest> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        EMAIL_URL,
                        entity,
                        String.class
                );

        LOGGER.info("Routee EMAIL response: {}", response);

        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error("Email send to {} failed", to);
            return false;
        }

        return true;
    }
}
