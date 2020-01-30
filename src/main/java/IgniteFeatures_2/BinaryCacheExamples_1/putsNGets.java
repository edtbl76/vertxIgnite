package IgniteFeatures_2.BinaryCacheExamples_1;

import DemoUtils.DemoUtils;
import DemoUtils.Models.Team;
import io.vertx.reactivex.core.AbstractVerticle;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.lang.IgniteFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class putsNGets extends AbstractVerticle {

    private static final String CACHE_NAME = "SuperBowl";

    public static void main(String[] args) {
        DemoUtils demo = new DemoUtils();
        demo.setupVerticle(putsNGets.class.getName());
    }

    @Override
    public void start() {

        try (Ignite ignite = Ignition.start()) {

            // Let's build a cache
            CacheConfiguration<Integer, Team> cacheConfiguration = new CacheConfiguration<>();

            // PARTITIONED distributes the data across all nodes. ATOMIC = ACID!
            cacheConfiguration
                    .setCacheMode(CacheMode.PARTITIONED)
                    .setName(CACHE_NAME)
                    .setAtomicityMode(CacheAtomicityMode.ATOMIC);

            try (IgniteCache<Integer, Team> igniteCache = ignite.getOrCreateCache(cacheConfiguration)) {
                System.out.println("\n\n\n");
                System.out.println("----------");
                System.out.println("Non-Binary");
                System.out.println("----------");

                putGet(igniteCache);
                putGetAll(igniteCache);


                System.out.println("----------");
                System.out.println("  Binary");
                System.out.println("----------");

                putGetBinary(igniteCache);
                putGetAllBinary(igniteCache);

                System.out.println("\n\n\n");
            } finally {
                ignite.destroyCache(CACHE_NAME);
            }
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    /*
            Simple Example 1: Just an Easy Put/Get
         */
    private static void putGet(IgniteCache<Integer, Team> igniteCache) {
        // Create a Team.
        Team team = DemoUtils.getChiefs();

        // Put it in the cache
        igniteCache.put(1, team);

        // Get It Out
        Team teamFromCache = igniteCache.get(1);

        // Output for Posterity
        System.out.println("Patrick Mahomes plays for: " + teamFromCache);

    }


    /*
        Basic Example 2: Put/Get (Multiple)
     */
    private static void putGetAll(IgniteCache<Integer, Team> igniteCache) {

        /*
            Go get a collection of objects in a List.
         */
        List<Team> teams = new ArrayList<>(DemoUtils.getTeams());

        /*
            Put the objects in a map
         */
        Map<Integer, Team> map = new ConcurrentHashMap<>();
        map.put(1, teams.get(0));
        map.put(2, teams.get(1));

        /*
            Put the map in the cache. has to be a map! NOTE: the asynchronous call returns an ignite Future.
         */
        igniteCache.putAllAsync(map);

        /*
            Get them asynchronously
         */
        IgniteFuture<Map<Integer, Team>> igniteFuture = igniteCache.getAllAsync(map.keySet());

        System.out.println("These teams are playing in the Super Bowl: ");
        for (Team team : igniteFuture.get().values()) {
            System.out.println("\t" + team);
        }

    }

    /*
        Binary Example 1: No Deserialization is Required.
     */
    private static void putGetBinary(IgniteCache<Integer, Team> igniteCache) {

        // Create a Team
        Team team = DemoUtils.getNiners();

        // Put it in the cache
        igniteCache.put(1, team);

        // Move it to binary and then get it.
        IgniteCache<Integer, BinaryObject> binaryCache = igniteCache.withKeepBinary();
        BinaryObject result = binaryCache.get(1);

        /*
            Cherry Pick the field we want and then print for posterity
         */
        String mascot = result.field("mascot");
        System.out.println("George Kittle plays for the " + mascot);

    }

    /*
        Binary Example 2: Stuffing Bulk tasks into Binary data stream.
     */
    private static void putGetAllBinary(IgniteCache<Integer, Team> igniteCache) {
        /*
            Get a team.
         */
        List<Team> teams = new ArrayList<>(DemoUtils.getTeams());

        /*
            Do the Map dance again, and then put the map into the cache
         */
        Map<Integer, Team> map = new ConcurrentHashMap<>();
        map.put(1, teams.get(0));
        map.put(2, teams.get(1));
        igniteCache.putAllAsync(map);

        /*
            Do the "toBinary" dance... AGAIN.
         */
        IgniteCache<Integer, BinaryObject> binaryCache = igniteCache.withKeepBinary();

        /*
            GET the BinaryObjects FROM the cache
         */
        IgniteFuture<Map<Integer, BinaryObject>> igniteFuture = binaryCache.getAllAsync(map.keySet());

        /*
            Iterate through the map and cherry pick the fields I want.
            - remember that we are doing this asynchronously
         */
        System.out.println("The following cities are represented in the Super Bowl: ");
        for(BinaryObject binaryObject : igniteFuture.get().values()) {
            System.out.println("\t" + binaryObject.field("city"));
        }


    }
}

