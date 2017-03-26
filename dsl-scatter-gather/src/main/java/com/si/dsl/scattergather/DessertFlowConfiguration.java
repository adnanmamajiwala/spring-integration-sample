package com.si.dsl.scattergather;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class DessertFlowConfiguration {

    private final KitchenService kitchenService;

    public DessertFlowConfiguration(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @Bean
    public MessageChannel distributionChannelDessertFlow() {
        return MessageChannels.executor("dessert-flow", dessertExecutor()).get();
    }

    @Bean
    public ThreadPoolTaskExecutor dessertExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(3);
        pool.setMaxPoolSize(3);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setThreadNamePrefix("SG - Dessert - ");
        return pool;
    }

    @Bean
    public IntegrationFlow dessertFlow() {
        return IntegrationFlows
                .from(distributionChannelDessertFlow())
                .split("payload.dessert")
                .handle(kitchenService, "prepareDessert")
                .get();
    }
}
