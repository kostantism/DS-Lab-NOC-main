package gr.hua.dit.noc.config;

import gr.hua.dit.noc.core.EmailService;
import gr.hua.dit.noc.core.impl.MockEmailService;
//import gr.hua.dit.noc.core.impl.RouteeEmailService;
import gr.hua.dit.noc.core.impl.SendGridEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class EmailServiceSelector {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceSelector.class);

    @Bean
    public EmailService emailService(
            EmailProperties emailProperties,
            SendGridEmailService sendGridEmailService,
            MockEmailService mockEmailService) {

        if ("sendgrid".equalsIgnoreCase(emailProperties.getProvider())) {
            LOGGER.info("SendGridEmailService is the default implementation of EmailService");
            return sendGridEmailService;
        } else {
            LOGGER.info("MockEmailService is the default implementation of EmailService");
            return mockEmailService;
        }
    }

}
