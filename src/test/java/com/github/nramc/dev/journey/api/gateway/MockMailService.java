package com.github.nramc.dev.journey.api.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MockMailService extends MailService {

    public MockMailService() {
        super(null, null, null);
    }

    @Override
    public void sendSimpleEmail(List<String> toAddresses, String subject, String body) {
        log.info("No operation required for sendSimpleEmail()");
    }

    @Override
    public void sendEmailUsingTemplate(String template, String to, String subject, Map<String, Object> variables) {
        log.info("No operation required for sendEmailUsingTemplate()");
    }


}
