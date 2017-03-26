package com.si.dsl.scattergather;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class DishFlowConfiguration {

    private final KitchenService kitchenService;

    public DishFlowConfiguration(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @Bean
    public ThreadPoolTaskExecutor dishExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(4);
        pool.setMaxPoolSize(4);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setThreadNamePrefix("SG - Dish - ");
        return pool;
    }

    @Bean
    public MessageChannel distributionChannelDishFlow() {
        return MessageChannels.executor("dish-flow", dishExecutor()).get();
    }

    @Bean
    public IntegrationFlow dishFlow() {
        return IntegrationFlows
                .from(distributionChannelDishFlow())
                .split("payload.dish")
                .handle(kitchenService, "prepareDish")
                .get();
    }
}
