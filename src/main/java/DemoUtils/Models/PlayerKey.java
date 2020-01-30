package DemoUtils.Models;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

import java.util.Objects;

public class PlayerKey {

    private int id;

    // This is required for collocated joins!
    @AffinityKeyMapped
    private int teamId;

    public PlayerKey() {}

    public PlayerKey(int id, int teamId) {
        this.id = id;
        this.teamId = teamId;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerKey playerKey = (PlayerKey) o;
        return id == playerKey.id &&
                teamId == playerKey.teamId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teamId);
    }

    @Override
    public String toString() {
        return "PlayerKey{" +
                "id=" + id +
                ", teamId=" + teamId +
                '}';
    }
}
