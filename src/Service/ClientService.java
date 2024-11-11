package Service;

import java.util.ArrayList;
import java.util.List;

import Models.Candidate;
import controller.ClientController;

public class ClientService {
    @SuppressWarnings("unused")
    public static void main (String[] args) throws Exception {
        Candidate candidate1 = new Candidate("Candidato 1");
        Candidate candidate2 = new Candidate("Candidato 2");

        List<Candidate> candidatesList = new ArrayList<>();
        candidatesList.add(candidate1);
        candidatesList.add(candidate2);

        ClientController client = new ClientController(candidatesList);
    }
}
