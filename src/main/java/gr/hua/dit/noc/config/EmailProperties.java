package gr.hua.dit.noc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

    private String provider;
    private SendGrid sendgrid;

    public static class SendGrid {
        private String apiKey;
        private String from;

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
    }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public SendGrid getSendgrid() { return sendgrid; }
    public void setSendgrid(SendGrid sendgrid) { this.sendgrid = sendgrid; }
}

