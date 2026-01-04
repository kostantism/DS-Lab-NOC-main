package gr.hua.dit.noc.core;

public interface EmailService {
    boolean sendEmail(String to, String subject, String content);
}

