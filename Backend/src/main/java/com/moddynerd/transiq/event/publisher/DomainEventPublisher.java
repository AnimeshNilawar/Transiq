package com.moddynerd.transiq.event.publisher;

import com.moddynerd.transiq.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
