package IgniteFeatures_2.ComputeExamples_2;

import org.apache.ignite.IgniteException;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.lang.IgniteBiTuple;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComputeAverageSalary extends ComputeTaskSplitAdapter<Collection<BinaryObject>, Long> {
    @Override
    protected Collection<? extends ComputeJob> split(int i, Collection<BinaryObject> binaryObjects) throws IgniteException {

        Collection<getPlayerData> jobs = new ArrayList<>();
        Collection<BinaryObject> players = new ArrayList<>();

        for (BinaryObject binaryObject : binaryObjects) {
            players.add(binaryObject);

            if (players.size() > 3) {
                jobs.add(new getPlayerData(players));
                players = new ArrayList<>(3);
            }
        }
        if (!players.isEmpty())
            jobs.add(new getPlayerData(players));

        return jobs;
    }

    @Nullable
    @Override
    public Long reduce(List<ComputeJobResult> list) throws IgniteException {
        long sum = 0;
        int count = 0;

        for (ComputeJobResult result : list) {
            IgniteBiTuple<Long, Integer> tuple = result.getData();
            sum += tuple.get1();
            count += tuple.get2();
        }
        return sum / count;
    }

    private static class getPlayerData extends ComputeJobAdapter {

        private final Collection<BinaryObject> players;

        private getPlayerData(Collection<BinaryObject> players) {
            this.players = players;
        }

        @Override
        public Object execute() throws IgniteException {
            long sum = 0;
            int count = 0;

            for (BinaryObject player : players) {
                System.out.println("Processing :" + player.field("name"));

                switch (String.valueOf(player.field("position"))) {
                    case "QB":
                        sum += 10000000;
                        break;
                    case "RB":
                        sum += 5000000;
                        break;
                    default:
                        sum += 1000000;
                        break;
                }
                count++;
            }

            return new IgniteBiTuple<>(sum, count);
        }
    }
}
