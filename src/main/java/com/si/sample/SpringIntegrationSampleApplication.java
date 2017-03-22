package com.si.sample;

import com.si.sample.models.Dish;
import com.si.sample.models.Drink;
import com.si.sample.models.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication
@IntegrationComponentScan
public class SpringIntegrationSampleApplication {

    public static void main(String[] args) throws Exception {
//        SpringApplication.run(SpringIntegrationSampleApplication.class, args);

        ConfigurableApplicationContext ctx = SpringApplication.run(IntegrationFlowConfiguration.class, args);

        CafeService cafe = ctx.getBean(CafeService.class);
        for (int i = 1; i <= 100; i++) {
            Order order = new Order(i, new Drink(i, "Coffee"), new Dish(i, "Sandwich"));
            cafe.placeOrder(order);
        }

        System.out.println("Hit 'Enter' to terminate");
        System.in.read();
        ctx.close();
    }
}
