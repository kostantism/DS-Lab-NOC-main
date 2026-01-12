package gr.hua.dit.noc.core;

import gr.hua.dit.noc.core.model.SendEmailRequest;
import gr.hua.dit.noc.core.model.SendEmailResult;

public interface EmailService {
    SendEmailResult send(final SendEmailRequest sendEmailRequest);
}

