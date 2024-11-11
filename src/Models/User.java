package Models;

import java.io.Serializable;

public class User implements Serializable {
    @SuppressWarnings("unused")
    private static final Long serialVersionUID = 1L;
    private String name;
    private String cpf;
    private Candidate candidate;

    public User (String name, String cpf) {
        this.name = name;
        this.cpf = cpf;
    }


    public String getName() {
        return this.name;
    }

    public String getCpf() {
        return this.cpf;
    }

    public String getCandidateName () {
        return this.candidate.getName();
    }
    
    public void setCandidate (Candidate c) {
        this.candidate = c;
    }
}
