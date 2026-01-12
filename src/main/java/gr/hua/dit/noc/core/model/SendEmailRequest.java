package gr.hua.dit.noc.core.model;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;

public record SendEmailRequest(
        @NonNull @NotBlank String to,
        @NonNull @NotBlank String subject,
        @NonNull @NotBlank String content
) {}