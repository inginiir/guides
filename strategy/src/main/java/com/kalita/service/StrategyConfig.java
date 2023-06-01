package com.kalita.service;

import com.kalita.model.RecipientType;
import com.kalita.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class StrategyConfig {

    private final List<NotificationService> notificationServices;

    @Bean
    public Map<RecipientType, NotificationService> buildStrategyMap() {
        return notificationServices.stream()
                .collect(Collectors.toMap(NotificationService::getType, Function.identity()));
    }
}
