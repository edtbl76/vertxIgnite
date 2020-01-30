package VertxEventBus_1.BasicExample_1;


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
        vertx.setPeriodic(
                1000,
                v -> eventBus
                        .rxRequest("ignite", "The misadventures of Ignitio Hornblower")
                        .subscribe());
    }



}
