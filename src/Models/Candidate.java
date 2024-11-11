package Models;

import java.io.Serializable;

public class Candidate implements Serializable{
    @SuppressWarnings("unused")
    private static final Long serialVersionUID = 1L;
    private String name;
    private Integer votesCounter;

    public Candidate (String name) {
        this.name = name;
        votesCounter = 0;
    }

    public Candidate (String name, Integer votesCounter) {
        this.name = name;
        this.votesCounter = votesCounter;
    }

    public void setName (String name) {
        this.name = name;
    }
    
    public String getName () {
        return this.name;
    }

    public void incrementVotesCounter () {
        this.votesCounter++;
    }

    public Integer getVotesCount () {
        return this.votesCounter;
    }

}
