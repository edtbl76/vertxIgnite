package VertxEventBus_1.BasicExample_1;

import DemoUtils.DemoUtils;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;

public class Consumer extends AbstractVerticle {

    public static void main(String[] args) {
        DemoUtils demo = new DemoUtils();
        demo.setupVerticle(Consumer.class.getName());

    }

    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("ignite", message -> System.out.println("RCVD: Consumer 1 - " + message.body()));
        eventBus.consumer("ignite", message -> System.out.println("RCVD: Consumer 2 - " + message.body()));
        eventBus.consumer("ignite", message -> System.out.println("RCVD: Consumer 3 - " + message.body()));
    }
}
