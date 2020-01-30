package VertxEventBus_1.P2PExample_2;

import DemoUtils.DemoUtils;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;

public class Producer extends AbstractVerticle {

    public static void main(String[] args) {
         DemoUtils demo = new DemoUtils();
         demo.setupVerticle(Producer.class.getName());
    }

    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();

        vertx.setPeriodic(1000,
                v -> eventBus.rxRequest("p2p-ignite", "ping")
                .subscribe(
                        objectMessage -> System.out.println("RESP RCVD: " + objectMessage.body()),
                        Throwable::printStackTrace

        ));

    }
}
