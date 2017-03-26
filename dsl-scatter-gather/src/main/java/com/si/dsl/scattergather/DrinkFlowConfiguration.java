package com.si.dsl.scattergather;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class DrinkFlowConfiguration {

    private final KitchenService kitchenService;

    public DrinkFlowConfiguration(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @Bean
    public MessageChannel distributionChannelDrinkFlow() {
        return MessageChannels.executor("drink-flow", drinkExecutor()).get();
    }

    @Bean
    public ThreadPoolTaskExecutor drinkExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(3);
        pool.setMaxPoolSize(3);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setThreadNamePrefix("SG - Drink - ");
        return pool;
    }

    @Bean
    public IntegrationFlow drinkFlow() {
        return IntegrationFlows
                .from(distributionChannelDrinkFlow())
                .split("payload.drink")
                .handle(kitchenService, "prepareDrink")
                .get();
    }

}
