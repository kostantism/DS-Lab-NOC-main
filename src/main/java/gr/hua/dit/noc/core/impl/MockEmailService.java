package gr.hua.dit.noc.core.impl;

import gr.hua.dit.noc.core.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mock")
public class MockEmailService implements EmailService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MockEmailService.class);

    @Override
    public boolean sendEmail(String to, String subject, String content) {
        LOGGER.info("MOCK EMAIL to={} subject={} content={}", to, subject, content);
        return true;
    }
}

