package com.github.nramc.dev.journey.api.shared.mail;

import jakarta.mail.MessagingException;

import java.util.Map;

/**
 * Shared abstraction for sending template-based emails.
 * Lives in {@code shared} so both {@code account} and {@code notification} can reference it
 * without creating a module cycle.
 */
public interface MailSender {

    // todo: move it to notification module and with proper DTO parameter
    void sendEmailUsingTemplate(String template, String to, String subject, Map<String, Object> placeholders)
            throws MessagingException;
}
