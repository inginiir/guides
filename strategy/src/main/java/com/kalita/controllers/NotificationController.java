package com.kalita.controllers;

import com.kalita.model.Destination;
import com.kalita.model.RecipientType;
import com.kalita.services.DestinationService;
import com.kalita.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationController {

    private final Map<RecipientType, NotificationService> notificationServices;
    private final DestinationService destinationService;

    @Scheduled(cron = "0 * * ? * *")
    public void sendOutNotifications() {
        List<Destination> destinations = destinationService.getDestinations();
        String message = checkAnomalies();
        destinations.forEach(destination -> destination.getRecipients()
                .forEach(recipient -> notificationServices.get(recipient.getType())
                        .sendMessage(recipient, message)));
    }

    private String checkAnomalies() {
        // checking metrics
        return "Some results of checking";
    }
}
