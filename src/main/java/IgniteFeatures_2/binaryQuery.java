package IgniteFeatures_2;

import DemoUtils.DemoUtils;
import DemoUtils.Models.Player;
import DemoUtils.Models.PlayerKey;
import DemoUtils.Models.Team;
import io.vertx.reactivex.core.AbstractVerticle;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.CacheKeyConfiguration;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.lang.IgniteFuture;


import javax.cache.Cache;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class binaryQuery extends AbstractVerticle {

    private static final String TEAM_CACHE = "Teams";
    private static final String PLAYER_CACHE = "Players";

    public static void main(String[] args) {
         DemoUtils demo = new DemoUtils();
         demo.setupVerticle(binaryQuery.class.getName());
    }

    @Override
    public void start() {

        // Start Ignite Node.
        try (Ignite ignite = Ignition.start()) {
            System.out.println("\n\n\n");

            // Set up non-binary caches (team and player)
            CacheConfiguration<Integer, Team> teamCacheConfiguration = new CacheConfiguration<>();
            teamCacheConfiguration
                    .setCacheMode(CacheMode.PARTITIONED)
                    .setName(TEAM_CACHE)
                    .setQueryEntities(Collections.singletonList(DemoUtils.getTeamQueryEntity()));


            CacheConfiguration<PlayerKey, Player> playerCacheConfiguration = new CacheConfiguration<>();
            playerCacheConfiguration
                    .setCacheMode(CacheMode.PARTITIONED)
                    .setName(PLAYER_CACHE)
                    .setQueryEntities(Collections.singletonList(DemoUtils.getPlayerQueryEntity()))
                    .setKeyConfiguration(new CacheKeyConfiguration(PlayerKey.class));


            // Do some work with the caches
            try (IgniteCache<Integer, Team> teamIgniteCache = ignite.getOrCreateCache(teamCacheConfiguration);
                 IgniteCache<PlayerKey, Player> playerIgniteCache = ignite.getOrCreateCache(playerCacheConfiguration)) {


                seedCaches(teamIgniteCache, playerIgniteCache);


                System.out.println(DemoUtils.getPlayers().keySet());
                IgniteFuture<Map<PlayerKey, Player>> igniteFuture = playerIgniteCache.getAllAsync(DemoUtils.getPlayers().keySet());
                igniteFuture.get().values().forEach(System.out::println);

                System.out.println(teamIgniteCache.get(1));


                IgniteCache<BinaryObject, BinaryObject> binaryCache = playerIgniteCache.withKeepBinary();
                fieldBasedQuery(binaryCache);
                joinQuery(binaryCache);
                textQuery(binaryCache);


            } finally {
                ignite.destroyCaches(Arrays.asList(TEAM_CACHE, PLAYER_CACHE));
            }

            System.out.println("\n\n\n");
        }
    }

    private static void seedCaches(IgniteCache<Integer, Team> teamCache, IgniteCache<PlayerKey, Player> playerCache) {
        teamCache.putAll(Map.of(
                1, DemoUtils.getNiners(),
                2, DemoUtils.getChiefs()
        ));


        playerCache.putAll(DemoUtils.getPlayers());
    }


    private static void fieldBasedQuery(IgniteCache<BinaryObject, BinaryObject> igniteCache) {
        SqlFieldsQuery query = new SqlFieldsQuery("select name, position from Player");
        System.out.println(query);

        QueryCursor<List<?>> players = igniteCache.query(query);
        System.out.println("Field Query: ");

        for (List<?> row : players.getAll()) {
            System.out.println("\t[" + row.get(1) + "]\t" + row.get(0));
        }

        System.out.println("\n\n");

    }

    private static void joinQuery(IgniteCache<BinaryObject, BinaryObject> igniteCache) {

        System.out.println("Join Query Example: (Collocated Join)");
        SqlFieldsQuery query = new SqlFieldsQuery(
                "select p.* from Player p, \"" + TEAM_CACHE + "\".Team as t where p.teamId = t.keyId and t.city = ?"

        );
        QueryCursor<List<?>> players = igniteCache.query(query.setArgs("San Francisco"));

        for (List<?> row : players.getAll()) {
            System.out.println("\t" + row);
        }
    }


    private static void textQuery(IgniteCache<BinaryObject, BinaryObject> igniteCache) {

        System.out.println("Text Search: ");
        TextQuery<BinaryObject, BinaryObject> query = new TextQuery<>(Player.class, "QB");
        QueryCursor<Cache.Entry<BinaryObject, BinaryObject>> playcallers = igniteCache.query(query);

        for (Cache.Entry<BinaryObject, BinaryObject> entry : playcallers.getAll()) {
            System.out.println("\t" + entry.getValue().deserialize());
        }

    }





}
