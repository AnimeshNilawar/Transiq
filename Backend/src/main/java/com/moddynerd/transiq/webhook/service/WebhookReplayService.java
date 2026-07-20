package com.moddynerd.transiq.webhook.service;

import java.util.UUID;

public interface WebhookReplayService {

    void replayEvent(UUID eventId);

}
