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
    public IntegrationFlow orders() {
        return IntegrationFlows
                .from("orders.input")
                .routeToRecipients(new Consumer<RecipientListRouterSpec>() {
                    @Override
                    public void accept(RecipientListRouterSpec recipientListRouterSpec) {
                        recipientListRouterSpec.recipient("drink-flow").applySequence(true);
                        recipientListRouterSpec.recipient("dish-flow").applySequence(true);
                        recipientListRouterSpec.recipient("dessert-flow").applySequence(true);
                    }
                })
                .get();
    }

    @Bean
    public IntegrationFlow drinkFlow() {
        return IntegrationFlows
                .from(MessageChannels.executor("drink-flow", executor()))
                .split("payload.drink")
                .handle(kitchenService, "prepareDrink")
                .channel("output-flow")
                .get();
    }

    @Bean
    public IntegrationFlow dishFlow() {
        return IntegrationFlows
                .from(MessageChannels.executor("dish-flow", executor()))
                .split("payload.dish")
                .handle(kitchenService, "prepareDish")
                .channel("output-flow")
                .get();
    }


    @Bean
    public IntegrationFlow dessertFlow() {
        return IntegrationFlows
                .from(MessageChannels.executor("dessert-flow", executor()))
                .split("payload.dessert")
                .handle(kitchenService, "prepareDessert")
                .channel("output-flow")
                .get();
    }


    @Bean
    public IntegrationFlow resultFlow() {
        return IntegrationFlows
                .from("output-flow")
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
    public ThreadPoolTaskExecutor executor(){
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setMaxPoolSize(5);
        pool.setCorePoolSize(5);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setThreadNamePrefix("Restaurant - ");
        return pool;
    }
}
