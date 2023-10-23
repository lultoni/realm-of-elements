import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StartWindow extends JFrame {
    private JComboBox<String> player1ComboBox;
    private JComboBox<String> player2ComboBox;

    public StartWindow() {
        setTitle("Realm of Elements");
        init();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the window
        setUndecorated(true); // Remove window decorations
        setVisible(true);
    }

    private void init() {
        // TODO Start Screen Music and stopping it again
        JPanel mainPanel = new JPanel(new GridBagLayout());
        JPanel leftPanel = new JPanel();
        JPanel middlePanel = new JPanel();
        JPanel rightPanel = new JPanel();

        ArrayList<String> players = new ArrayList<>();
        for (Player player: DBH.getAllPlayers()) {
            players.add(player.name);
        }

        // Create the title label
        JLabel titleLabel = new JLabel("Realm of Elements");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 100));
        titleLabel.setVerticalAlignment(JLabel.CENTER);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create the JComboBox and Start Game button panel
        JPanel playerPanel = new JPanel(new GridLayout(1, 0));
        player1ComboBox = new JComboBox<>(players.toArray(new String[0]));
        player2ComboBox = new JComboBox<>(players.toArray(new String[0]));
        JButton startGameButton = getStartGameButton();

        // Create the Settings and Exit button panel
        JPanel settingsButtonsPanel = new JPanel(new GridLayout(1, 0));
        JPanel exitButtonsPanel = new JPanel(new GridLayout(1, 0));
        JButton settingsButton = new JButton("Settings (WIP)");
        JButton exitButton = new JButton("Exit");

        settingsButton.addActionListener(e -> {
            // TODO add settings (add new player, change music volume, change sound volume)
            System.out.println("Settings button pressed.");
        });

        exitButton.addActionListener(e -> {
            System.out.println("Exit button pressed.");
            System.exit(0); // Close the program
        });

        // Add components to panels
        playerPanel.add(player1ComboBox);
        playerPanel.add(startGameButton);
        playerPanel.add(player2ComboBox);

        JLabel player1Info = new JLabel();
        player1Info.setText("ELO: " + getPlayer1().elo + ", Games Played: " + getPlayer1().gamesPlayed);
        player1Info.setVerticalAlignment(JLabel.CENTER);
        player1Info.setHorizontalAlignment(JLabel.CENTER);

        JLabel player2Info = new JLabel();
        player2Info.setText("ELO: " + getPlayer2().elo + ", Games Played: " + getPlayer2().gamesPlayed);
        player2Info.setVerticalAlignment(JLabel.CENTER);
        player2Info.setHorizontalAlignment(JLabel.CENTER);

        player1ComboBox.addActionListener(e -> {
            String selectedPlayerName = (String) player1ComboBox.getSelectedItem();
            Player player = getPlayerByName(selectedPlayerName);
            if (player != null) {
                player1Info.setText("ELO: " + player.elo + ", Games Played: " + player.gamesPlayed);
                player1Info.revalidate(); // Ensure the changes are reflected immediately
            }
        });

        player2ComboBox.addActionListener(e -> {
            String selectedPlayerName = (String) player2ComboBox.getSelectedItem();
            Player player = getPlayerByName(selectedPlayerName);
            if (player != null) {
                player2Info.setText("ELO: " + player.elo + ", Games Played: " + player.gamesPlayed);
                player2Info.revalidate(); // Ensure the changes are reflected immediately
            }
        });

        Font font = new Font("Arial", Font.PLAIN, 20);
        player1ComboBox.setFont(font);
        player2ComboBox.setFont(font);
        settingsButton.setFont(font);
        exitButton.setFont(font);
        player1Info.setFont(font);
        player2Info.setFont(font);
        startGameButton.setFont(font);

        settingsButtonsPanel.add(player1Info);
        settingsButtonsPanel.add(settingsButton);
        settingsButtonsPanel.add(player2Info);
        exitButtonsPanel.add(new JPanel()); // Empty panel
        exitButtonsPanel.add(exitButton);
        exitButtonsPanel.add(new JPanel()); // Empty panel

        middlePanel.setLayout(new GridLayout(0, 1));
        middlePanel.add(titleLabel);
        middlePanel.add(playerPanel);
        middlePanel.add(settingsButtonsPanel);
        middlePanel.add(exitButtonsPanel);

        // Add panels to the main panel
        mainPanel.add(leftPanel, createGBC(1, 1, 0, 0));
        mainPanel.add(middlePanel, createGBC(3, 1, 1, 0));
        mainPanel.add(rightPanel, createGBC(1, 1, 4, 0));

        add(mainPanel);
    }

    private Player getPlayerByName(String selectedPlayerName) {
        ArrayList<Player> allPlayers = DBH.getAllPlayers();
        for (Player player: allPlayers) {
            if (player.name.equals(selectedPlayerName)) return player;
        }
        return null;
    }

    private JButton getStartGameButton() {
        JButton startGameButton = new JButton("Start Game");

        startGameButton.addActionListener(e -> {
            String selectedPlayer1 = (String) player1ComboBox.getSelectedItem();
            String selectedPlayer2 = (String) player2ComboBox.getSelectedItem();

            if (!selectedPlayer1.equals(selectedPlayer2)) {
                dispose();
                WAVPlayer.play("GameStart.wav");
                Main.play();
            } else {
                JOptionPane.showMessageDialog(StartWindow.this, "Please select different players for both sides.");
            }
        });
        return startGameButton;
    }

    public Player getPlayer1() {
        ArrayList<Player> allPlayers = DBH.getAllPlayers();
        for (Player player: allPlayers) {
            if (player.name.equals(player1ComboBox.getSelectedItem())) return player;
        }
        return null;
    }

    public Player getPlayer2() {
        ArrayList<Player> allPlayers = DBH.getAllPlayers();
        for (Player player: allPlayers) {
            if (player.name.equals(player2ComboBox.getSelectedItem())) return player;
        }
        return null;
    }
    private GridBagConstraints createGBC(int gridWidth, int gridHeight, int gridX, int gridY) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.gridheight = gridHeight;
        gbc.gridwidth = gridWidth;
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }

}