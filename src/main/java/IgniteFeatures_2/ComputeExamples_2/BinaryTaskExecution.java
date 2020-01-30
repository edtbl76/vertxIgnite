package IgniteFeatures_2.ComputeExamples_2;

import DemoUtils.DemoUtils;
import DemoUtils.Models.Player;
import io.vertx.reactivex.core.AbstractVerticle;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;

import java.util.Collection;

public class BinaryTaskExecution extends AbstractVerticle {

    public static void main(String[] args) {
        DemoUtils demo = new DemoUtils();
        demo.setupVerticle(BinaryTaskExecution.class.getName());
    }

    @Override
    public void start() {

        try (Ignite ignite = Ignition.start()) {
            Collection<Player> seahawks = DemoUtils.getSeahawks();

            System.out.println("\nPrinting an Object: ");
            seahawks.forEach(System.out::println);

            // Serializing to binary.
            Collection<BinaryObject> binarySeahawks = ignite.binary().toBinary(seahawks);

            Long average = ignite.compute(ignite.cluster().forRemotes())
                    .execute(new ComputeAverageSalary(), binarySeahawks);

            System.out.println("Average Salary: " + average);
        }
    }
}
