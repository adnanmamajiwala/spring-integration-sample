package com.si.dsl.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.AggregatorSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.RecipientListRouterSpec;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.stream.CharacterStreamWritingMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class IntegrationFlowConfiguration {

    private final OrderAggregator orderAggregator;
    private final KitchenService kitchenService;

    @Autowired
    public IntegrationFlowConfiguration(OrderAggregator orderAggregator, KitchenService kitchenService) {
        this.orderAggregator = orderAggregator;
        this.kitchenService = kitchenService;
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedDelay(1).get();
    }

    @Bean
    public MessageChannel drinkChannel() {
        return MessageChannels.executor("drink-flow", inputExecutor()).get();
    }

    @Bean
    public MessageChannel dishChannel() {
        return MessageChannels.executor("dish-flow", inputExecutor()).get();
    }

    @Bean
    public MessageChannel dessertChannel() {
        return MessageChannels.executor("dessert-flow", inputExecutor()).get();
    }

    @Bean
    public MessageChannel outputChannel() {
        return MessageChannels.executor("output-flow", outputExecutor()).get();
    }

    @Bean
    public IntegrationFlow orders() {
        return IntegrationFlows
                .from("orders.input")
                .routeToRecipients(new Consumer<RecipientListRouterSpec>() {
                    @Override
                    public void accept(RecipientListRouterSpec recipientListRouterSpec) {
                        recipientListRouterSpec.recipient("drinkChannel").applySequence(true);
                        recipientListRouterSpec.recipient("dishChannel").applySequence(true);
                        recipientListRouterSpec.recipient("dessertChannel").applySequence(true);
                    }
                })
                .get();
    }

    @Bean
    public IntegrationFlow drinkFlow() {
        return IntegrationFlows
                .from(drinkChannel())
                .split("payload.drink")
                .handle(kitchenService, "prepareDrink")
                .channel("outputChannel")
                .get();
    }

    @Bean
    public IntegrationFlow dishFlow() {
        return IntegrationFlows
                .from(dishChannel())
                .split("payload.dish")
                .handle(kitchenService, "prepareDish")
                .channel("outputChannel")
                .get();
    }

    @Bean
    public IntegrationFlow dessertFlow() {
        return IntegrationFlows
                .from(dessertChannel())
                .split("payload.dessert")
                .handle(kitchenService, "prepareDessert")
                .channel("outputChannel")
                .get();
    }

    @Bean
    public IntegrationFlow resultFlow() {
        return IntegrationFlows
                .from(outputChannel())
                .aggregate(new Consumer<AggregatorSpec>() {
                    @Override
                    public void accept(AggregatorSpec aggregatorSpec) {
                        aggregatorSpec.processor(orderAggregator, null);
                    }
                })
                .handle(CharacterStreamWritingMessageHandler.stdout())
                .get();
    }

    @Bean
    public ThreadPoolTaskExecutor outputExecutor(){
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setMaxPoolSize(2);
        pool.setCorePoolSize(2);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setThreadNamePrefix("Restaurant - output -");
        return pool;
    }

    @Bean
    public ThreadPoolTaskExecutor inputExecutor(){
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setMaxPoolSize(3);
         pool.setCorePoolSize(3);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setThreadNamePrefix("Restaurant - inout - ");
        return pool;
    }
}
