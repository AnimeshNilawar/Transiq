package com.moddynerd.transiq.webhook.controller;

import com.moddynerd.transiq.webhook.service.WebhookReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/webhooks/events")
@RequiredArgsConstructor
public class WebhookReplayController {

    private final WebhookReplayService replayService;

    @PostMapping("/{eventId}/replay")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void replayEvent(@PathVariable UUID eventId) {

        replayService.replayEvent(eventId);

    }

}
