package VertxEventBus_1.P2PExample_2;

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

        eventBus.consumer("p2p-ignite", message -> {
            System.out.println("MESG RCVD: " + message.body());
            message.reply("pong!");
        });
    }
}
