package com.github.nramc.dev.journey.api.infrastructure.event;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;


@Component
@Slf4j
public class SimpleEventHandler {
    private final AtomicInteger invocationCount = new AtomicInteger();
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @ApplicationModuleListener
    public void handler(SimpleEvent event) {
        int count = invocationCount.incrementAndGet();
        log.info("SimpleEventHandler handler called with event {} (invocation #{})", event, count);
        // Fail first 2 times, succeed on 3rd
        if (count <= 2) {
            throw new RuntimeException("Simulated failure for retry test (attempt " + count + ")");
        }
        applicationEventPublisher.publishEvent(new SimpleEventSuccess(event.message()));
    }

    public int getInvocationCount() {
        return invocationCount.get();
    }

}
