package DemoUtils.Models;

public class Player {

    private String name;
    private String position;
    private Team team;

    public Player() {}

    public Player(String name, String position, Team team) {
        this.name = name;
        this.position = position;
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", team=" + team +
                '}';
    }
}
