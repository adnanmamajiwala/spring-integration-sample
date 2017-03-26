package com.si.dsl.scattergather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aggregator.AggregatingMessageHandler;
import org.springframework.integration.aggregator.MethodInvokingCorrelationStrategy;
import org.springframework.integration.aggregator.MethodInvokingMessageGroupProcessor;
import org.springframework.integration.aggregator.MethodInvokingReleaseStrategy;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.router.RecipientListRouter;
import org.springframework.integration.scattergather.ScatterGatherHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.integration.stream.CharacterStreamWritingMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;

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

    private MessageChannel distributionChannel1() {
        return MessageChannels.executor("drink-flow", executor()).get();
    }

    private MessageChannel distributionChannel2() {
        return MessageChannels.executor("dish-flow", executor()).get();
    }

    private MessageChannel distributionChannel3() {
        return MessageChannels.executor("dessert-flow", executor()).get();
    }

    private MessageChannel output() {
        return MessageChannels.executor("output-flow", executor()).get();
    }

    private MessageHandler distributor() {
        RecipientListRouter router = new RecipientListRouter();
        router.setApplySequence(true);
        router.setChannels(Arrays.asList(distributionChannel1(), distributionChannel2(), distributionChannel3()));
        return router;
    }

    private MessageHandler gatherer() {
        return new AggregatingMessageHandler(
                new MethodInvokingMessageGroupProcessor(orderAggregator, "output"),
                new SimpleMessageStore(),
                new MethodInvokingCorrelationStrategy(orderAggregator, "correlateBy"),
                new MethodInvokingReleaseStrategy(orderAggregator, "releaseChecker"));
    }

    private MessageHandler scatterGatherDistribution() {
        ScatterGatherHandler handler = new ScatterGatherHandler(distributor(), gatherer());
        handler.setOutputChannel(output());
        return handler;
    }

    @Bean
    public IntegrationFlow orders() {
        return IntegrationFlows
                .from("orders.input")
                .handle(scatterGatherDistribution())
                .get();
    }

    @Bean
    public IntegrationFlow dishFlow() {
        return IntegrationFlows
                .from(distributionChannel2())
                .split("payload.dish")
                .handle(kitchenService, "prepareDish")
                .get();
    }

    @Bean
    public IntegrationFlow drinkFlow() {
        return IntegrationFlows
                .from(distributionChannel1())
                .split("payload.drink")
                .handle(kitchenService, "prepareDrink")
                .get();
    }

    @Bean
    public IntegrationFlow dessertFlow() {
        return IntegrationFlows
                .from(distributionChannel3())
                .split("payload.dessert")
                .handle(kitchenService, "prepareDessert")
                .get();
    }

    @Bean
    public IntegrationFlow resultFlow() {
        return IntegrationFlows
                .from(output())
                .handle(CharacterStreamWritingMessageHandler.stdout())
                .get();
    }

    @Bean
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(5);
        pool.setMaxPoolSize(5);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setThreadNamePrefix("SG - Restaurant - ");
        return pool;
    }
}
