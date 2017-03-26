package com.si.dsl.scattergather;

import org.springframework.beans.factory.annotation.Qualifier;
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
public class MainFlowConfiguration {

    private final OrderAggregator orderAggregator;
    private final MessageChannel drinkChannel;
    private final MessageChannel dishChannel;
    private final MessageChannel dessertChannel;

    public MainFlowConfiguration(OrderAggregator orderAggregator,
                                 @Qualifier("distributionChannelDrinkFlow") MessageChannel drinkChannel,
                                 @Qualifier("distributionChannelDishFlow") MessageChannel dishChannel,
                                 @Qualifier("distributionChannelDessertFlow") MessageChannel dessertChannel) {
        this.orderAggregator = orderAggregator;
        this.drinkChannel = drinkChannel;
        this.dishChannel = dishChannel;
        this.dessertChannel = dessertChannel;
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedDelay(1).get();
    }

    @Bean
    public MessageChannel output() {
        return MessageChannels.executor("output-flow", outputExecutor()).get();
    }

    @Bean(name = "orders.input")
    public MessageChannel input() {
        return MessageChannels.executor("input-flow", inputExecutor()).get();
    }

    private MessageHandler distributor() {
        RecipientListRouter router = new RecipientListRouter();
        router.setApplySequence(true);
        router.setChannels(Arrays.asList(dessertChannel, dishChannel, drinkChannel));
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
                .from(input())
                .handle(scatterGatherDistribution())
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
    public ThreadPoolTaskExecutor outputExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(2);
        pool.setMaxPoolSize(2);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setThreadNamePrefix("SG - Output - ");
        return pool;
    }

    @Bean
    public ThreadPoolTaskExecutor inputExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(10);
        pool.setMaxPoolSize(10);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setThreadNamePrefix("SG - Input - ");
        return pool;
    }

}
