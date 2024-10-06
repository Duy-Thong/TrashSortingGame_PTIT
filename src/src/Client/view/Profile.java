package Client.view;

import Client.controller.ProfileController;

import javax.swing.*;
import java.awt.*;

public class Profile extends JFrame {
    private final String playerID;

    public Profile(String playerID) {
        this.playerID = playerID;
        initComponents();
        loadPlayerInfo();
    }

    private void initComponents() {
        setTitle("Player Profile");
        setSize(400, 300);
        setLayout(new GridLayout(6, 2));

        // Labels for displaying player info
        add(new JLabel("Username: "));
        add(new JLabel());
        add(new JLabel("Total Games: "));
        add(new JLabel());
        add(new JLabel("Total Wins: "));
        add(new JLabel());
        add(new JLabel("Total Score: "));
        add(new JLabel());
        add(new JLabel("Average Score: "));
        add(new JLabel());
        add(new JLabel("Account Created At: "));
        add(new JLabel());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void loadPlayerInfo() {
        // Use ProfileController to fetch player info
        ProfileController controller = new ProfileController();
        String[] playerInfo = controller.getPlayerProfile(playerID);

        // If valid data returned, set the labels
        if (playerInfo != null) {
            ((JLabel) getContentPane().getComponent(1)).setText(playerInfo[0]);  // Username
            ((JLabel) getContentPane().getComponent(3)).setText(playerInfo[1]);  // Total Games
            ((JLabel) getContentPane().getComponent(5)).setText(playerInfo[2]);  // Total Wins
            ((JLabel) getContentPane().getComponent(7)).setText(playerInfo[3]);  // Total Score
            ((JLabel) getContentPane().getComponent(9)).setText(playerInfo[4]);  // Average Score
            ((JLabel) getContentPane().getComponent(11)).setText(playerInfo[5]); // Created At
        } else {
            JOptionPane.showMessageDialog(this, "Failed to load player profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
