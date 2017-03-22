package com.si.sample;

import com.google.common.util.concurrent.Uninterruptibles;
import com.si.sample.models.Dish;
import com.si.sample.models.Drink;
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
import org.springframework.integration.dsl.support.GenericHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.stream.CharacterStreamWritingMessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class IntegrationFlowConfiguration {

    private final AtomicInteger hotDrinkCounter = new AtomicInteger();
    private final AtomicInteger coldDrinkCounter = new AtomicInteger();

    private OrderAggregator orderAggregator;

    @Autowired
    public IntegrationFlowConfiguration(OrderAggregator orderAggregator) {
        this.orderAggregator = orderAggregator;
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
                    }
                })
                .get();
    }

    @Bean
    public IntegrationFlow drinkFlow() {
        return IntegrationFlows
                .from(MessageChannels.executor("drink-flow", executor()))
                .split("payload.drink")
                .handle(new GenericHandler<Drink>() {

                    @Override
                    public Object handle(Drink drink, Map<String, Object> map) {
                        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                        System.out.println(Thread.currentThread().getName()
                                + " prepared cold drink #" + coldDrinkCounter.incrementAndGet()
                                + " for order #" + drink.getOrderNumber() + ": " + drink.getDrinkName());
                        return drink;
                    }
                })
                .channel("output")
                .get();
    }

    @Bean
    public IntegrationFlow dishFlow() {
        return IntegrationFlows
                .from(MessageChannels.executor("dish-flow", executor()))
                .split("payload.dish")
                .handle(new GenericHandler<Dish>() {

                    @Override
                    public Object handle(Dish dish, Map<String, Object> map) {
                        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
                        System.out.println(Thread.currentThread().getName()
                                + " prepared hot drink #" + hotDrinkCounter.incrementAndGet()
                                + " for order #" + dish.getOrderNumber() + ": " + dish.getDishName());
                        return dish;
                    }
                })
                .channel("output")
                .get();
    }

    @Bean
    public IntegrationFlow resultFlow() {
        return IntegrationFlows
                .from("output")
                .aggregate(new Consumer<AggregatorSpec>() {
                    @Override
                    public void accept(AggregatorSpec aggregatorSpec) {
                        aggregatorSpec.processor(orderAggregator);
                    }
                })
                .handle(CharacterStreamWritingMessageHandler.stdout())
                .get();
    }


    @Bean
    public ThreadPoolTaskExecutor executor(){
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setMaxPoolSize(10);
        pool.setCorePoolSize(10);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setThreadNamePrefix("Order - ");
        return pool;
    }
}
