package gr.hua.dit.noc.config;

import gr.hua.dit.noc.core.EmailService;
import gr.hua.dit.noc.core.impl.MockEmailService;
import gr.hua.dit.noc.core.impl.RouteeEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class EmailServiceSelector {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceSelector.class);

    @Bean
    public EmailService emailService(final RouteeEmailProperties routeeEmailProperties,
                                     final RouteeEmailService routeeEmailService,
                                     final MockEmailService mockEmailService) {

        if (routeeEmailProperties == null) throw new NullPointerException();
        if (routeeEmailService == null) throw new NullPointerException();
        if (mockEmailService == null) throw new NullPointerException();

        if (StringUtils.hasText(routeeEmailProperties.getAppId()) && StringUtils.hasText(routeeEmailProperties.getAppSecret())) {

            LOGGER.info("RouteeEmailService is the default implementation of EmailService");
            return routeeEmailService;

        } else {
            LOGGER.info("MockEmailService is the default implementation of EmailService");
            return mockEmailService;
        }
    }
}
