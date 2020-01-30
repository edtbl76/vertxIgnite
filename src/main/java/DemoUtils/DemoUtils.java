package DemoUtils;

import DemoUtils.Models.*;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.cache.QueryIndexType;
import org.apache.ignite.configuration.IgniteConfiguration;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class DemoUtils {

    /*
        Verticle and Generic ignite setup.
     */
    private IgniteConfiguration igniteConfiguration = new IgniteConfiguration();

    public void setupVerticle(String className) {

        VertxOptions vertxOptions = new VertxOptions()
                .setEventBusOptions(new EventBusOptions().setClustered(true))
                .setClusterManager(new IgniteClusterManager(igniteConfiguration));

        Consumer<Vertx> runner = cv -> {
            try {
                cv.deployVerticle(className);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        };

        Single<Vertx> vertx = Vertx.rxClusteredVertx(vertxOptions);
        vertx.subscribe(
                runner,
                Throwable::printStackTrace
        );
    }


    /*
        Just some Object Helpers.
     */
    public static Team getChiefs()  {
        return new Team(
                "Chiefs",
                "Kansas City",
                Conference.AFC,
                Division.WEST,
                new Timestamp(System.currentTimeMillis())
        );
    }

    public static Team getNiners() {
        return new Team(
                "49ers",
                "San Francisco",
                Conference.NFC,
                Division.WEST,
                new Timestamp(System.currentTimeMillis())
        );
    }

    public static Team getHawks() {
        return new Team(
                "Seahawks",
                "Seattle",
                Conference.NFC,
                Division.WEST,
                new Timestamp(System.currentTimeMillis())
        );
    }

    public static Collection<Team> getTeams() {
        return Arrays.asList(
                getChiefs(),
                getNiners()
        );
    }

    public static Map<PlayerKey, Player> getPlayers() {
        return Map.of(
                new PlayerKey(1, 1),
                new Player("Jimmy Garoppolo", "QB", getNiners()),
                new PlayerKey(2, 1),
                new Player("George Kittle", "TE", getNiners()),
                new PlayerKey(3, 1),
                new Player("Richard Sherman", "CB", getNiners()),
                new PlayerKey(4,2 ),
                new Player("Patrick Mahomes", "QB", getChiefs()),
                new PlayerKey(5, 2),
                new Player("Tyreek Hill", "WR", getChiefs()),
                new PlayerKey(6, 2),
                new Player("Frank Clark", "EDGE", getChiefs())
        );
    }

    public static Collection<Player> getSeahawks() {
        return Arrays.asList(
                new Player("Russell Wilson", "QB", getHawks()),
                new Player("Chris Carson", "RB", getHawks()),
                new Player("Will Dissly", "TE", getHawks()),
                new Player("Bobby Wagner", "MLB", getHawks()),
                new Player("Tyler Lockett", "WR", getHawks()),
                new Player("D.K. Metcalf", "WR", getHawks())
        );
    }

    /*
        Query Entities.
     */
    public static QueryEntity getPlayerQueryEntity() {
        return new QueryEntity()
                .setValueType(Player.class.getName())
                .setKeyType(PlayerKey.class.getName())
                .addQueryField("teamId", Integer.class.getName(), null)
                .addQueryField("name", String.class.getName(), null)
                .addQueryField("position", String.class.getName(), null)
                .addQueryField("team", Team.class.getName(), null)
                .setKeyFields(Collections.singleton("teamId"))
                .setIndexes(Arrays.asList(
                        new QueryIndex("name"),
                        // This is necessary for TextQuery
                        new QueryIndex("position", QueryIndexType.FULLTEXT),
                        new QueryIndex("team"),
                        new QueryIndex("teamId")
                ));
    }

    public static QueryEntity getTeamQueryEntity() {
        return new QueryEntity()
                .setValueType(Team.class.getName())
                .setKeyType(Integer.class.getName())
                .addQueryField("keyId", Integer.class.getName(), null)
                .addQueryField("mascot", String.class.getName(), null)
                .addQueryField("city", String.class.getName(), null)
                .addQueryField("conference", Enum.class.getName(), null)
                .addQueryField("division", Enum.class.getName(),null)
                .setKeyFieldName("keyId")
                .setIndexes(Collections.singletonList(new QueryIndex("mascot")));
    }


}
