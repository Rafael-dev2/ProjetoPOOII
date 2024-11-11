package controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.*;

import Models.Candidate;
import Models.User;
import Utils.DialogBox;

public class ClientController extends JFrame implements ActionListener{
    private JPanel panel;
    private JLabel title;
    private JLabel name;
    private JLabel cpf;
    private JTextField tname;
    private JTextField tcpf;
    private JComboBox<String> voteSelectBox;
    private JButton confirmButton;
    private JLabel warningLabel;
    private List<Candidate> candidatesList;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Boolean electionsRunning = false;
    private File configFile;

    public ClientController(List<Candidate> candidatesList) {
        super("Sistema de votação");
        setBounds(300, 90, 400, 400);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.configFile = new File("Config.conf");
        this.candidatesList = new ArrayList<>();
        this.candidatesList.addAll(candidatesList);
     
        panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        title = new JLabel("Sistema de votação");
        title.setFont(new Font("Calibri", Font.PLAIN, 30));
        title.setSize(300, 30);
        title.setLocation(80, 30);
        panel.add(title);

        name = new JLabel("Nome*");
        name.setFont(new Font("Calibri", Font.PLAIN, 20));
        name.setSize(100, 20);
        name.setLocation(25, 100);
        panel.add(name);

        tname = new JTextField();
        tname.setFont(new Font("Calibri",Font.PLAIN, 15));
        tname.setSize(190, 20);
        tname.setLocation(125, 100);
        tname.setEnabled(false);
        panel.add(tname);

        cpf = new JLabel("CPF*");
        cpf.setFont(new Font("Calibri", Font.PLAIN, 20));
        cpf.setSize(100, 20);
        cpf.setLocation(25, 150);
        panel.add(cpf);
        
        tcpf = new JTextField();
        tcpf.setFont(new Font("Calibri",Font.PLAIN, 15));
        tcpf.setSize(150, 20);
        tcpf.setLocation(125, 150);
        tcpf.setEnabled(false);
        panel.add(tcpf);

        voteSelectBox = new JComboBox<String>();
        candidatesList.forEach(c -> voteSelectBox.addItem(c.getName()));
        voteSelectBox.setFont(new Font("Calibri", Font.PLAIN, 15));
        voteSelectBox.setSize(100, 30);
        voteSelectBox.setLocation(125, 200);
        voteSelectBox.setEnabled(false);
        panel.add(voteSelectBox);

        warningLabel = new JLabel("Aviso: eleições não iniciadas!");
        warningLabel.setFont(new Font("Calibri", Font.PLAIN, 12));
        warningLabel.setSize(300, 30);
        warningLabel.setLocation(50,250);
        panel.add(warningLabel);

        confirmButton = new JButton("Confirmar");
        confirmButton.setFont(new Font("Calibri", Font.PLAIN, 15));
        confirmButton.setSize(100, 50);
        confirmButton.setLocation(125,300);
        confirmButton.setBackground(new Color(146, 245, 100));
        confirmButton.addActionListener(this);
        confirmButton.setEnabled(false);
        panel.add(confirmButton);

        setVisible(true);
        connectToServer();
    }

    public void connectToServer() {
        new Thread(() -> {
            try {
                String IP = "localhost";
                int port = 8080;
                if (configFile.exists()) {
                    FileInputStream input = new FileInputStream(configFile);
                    Properties config = new Properties();
                    config.load(input);
                    IP = config.getProperty("IP");
                    System.out.println(IP);
                     port = Integer.parseInt(config.getProperty("PORT"));
                }
                socket = new Socket(IP, port);
                System.out.println(socket.getInetAddress() + " conectado");

                electionsRunning = true;
                
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                while(electionsRunning) {
                    String command = (String) in.readObject();
                    System.out.println("Comando recebido do servidor: " + command);
                    processCommand(command);
                }
                
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERRO:: " + e.getMessage());
            }
        }).start();
    }

    private boolean isServerAvailable(String server, int port) {
        try (Socket socket = new Socket()) {

            socket.connect(new InetSocketAddress(server, port), 100);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void enableVote () {
        tname.setEnabled(true);
        tcpf.setEnabled(true);
        voteSelectBox.setEnabled(true);
        warningLabel.setText("");
        confirmButton.setEnabled(true);
    }

    private void disableVote () {
        tname.setEnabled(false);
        tcpf.setEnabled(false);
        voteSelectBox.setEnabled(false);
        warningLabel.setText("Aviso: eleições encerradas!");
        confirmButton.setEnabled(false);
    }

    private void disableVoteForClient () {
        tname.setEnabled(false);
        tcpf.setEnabled(false);
        voteSelectBox.setEnabled(false);
        warningLabel.setText("Voto Realizado!");
        confirmButton.setEnabled(false);
    }

    private void processCommand (String command) {
        switch (command) {
            case "begin":
                enableVote();
                break;
            case "end":
                disableVote();
                try {
                    socket.close();
                    System.out.println(socket.getInetAddress() + " desconectado");
                } 
                catch (Exception e) {
                    String str = "Erro inesperado";
                    SwingUtilities.invokeLater(() -> new DialogBox(this, str, "Não foi possível desconectar"));

                    this.dispose();
                    System.out.println(e.getMessage());
                }
                break;
            case "authentication":
                try {
                    int error = (int)in.readObject();
                    voteStatusFeedback(error);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
                break;
            default:
                break;
        }
    }

    public void voteStatusFeedback(int error) {
        String str;
        switch (error){
            case 0:
                str = String.format("Voto confirmado no %s!", voteSelectBox.getSelectedItem());

                SwingUtilities.invokeLater(() -> new DialogBox(this, str, "Confirmação de voto"));

                try {
                    socket.close();
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                disableVoteForClient();
                break;
            case 1:
                str = "Erro ao votar: Nome ou CPF inválido";
                SwingUtilities.invokeLater(() -> new DialogBox(this, str, "Voto não confirmado"));
                break;
            case 2:
                str = "Este usuário já realizou o voto";
                SwingUtilities.invokeLater(() -> new DialogBox(this, str, "Voto não confirmado"));
                break;

        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (tname.getText().equals("") || tcpf.getText().equals(""))
        {
            warningLabel.setText("Por favor, preencha todos os campos que contém \"*\"");
            warningLabel.setForeground(Color.RED);
        }
        else 
        {
            User user = new User(tname.getText(), tcpf.getText());
            String candidateSelected = (String)voteSelectBox.getSelectedItem();

            String strCommand = "validate";
            try {
                out.writeObject(strCommand);
                out.writeObject(user);
                out.writeObject(candidateSelected);
                out.flush();
            }
            catch (IOException exc) {
                System.out.println("ERRO:: " + exc.getMessage());
            }
        }
    }   
}
