package DemoUtils.Models;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicLong;

public class Team {

    private static final AtomicLong ID_GEN = new AtomicLong();

    private Long id;
    private String mascot;
    private String city;
    private Conference conference;
    private Division division;

    private Timestamp lastUpdated;

    public Team(String mascot) {
        id = ID_GEN.incrementAndGet();
        this.mascot = mascot;
    }

    public Team(long id, String mascot) {
        this.id = id;
        this.mascot = mascot;
    }

    public Team(String mascot, String city, Conference conference, Division division, Timestamp lastUpdated) {
        id = ID_GEN.incrementAndGet();
        this.mascot = mascot;
        this.city = city;
        this.conference = conference;
        this.division = division;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMascot() {
        return mascot;
    }

    public void setMascot(String mascot) {
        this.mascot = mascot;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", mascot='" + mascot + '\'' +
                ", city='" + city + '\'' +
                ", conference=" + conference +
                ", division=" + division +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}

