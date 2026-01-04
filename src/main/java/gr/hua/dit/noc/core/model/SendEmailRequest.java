package gr.hua.dit.noc.core.model;

public record SendEmailRequest(
        String to,
        String subject,
        String body,
        String from
) {}