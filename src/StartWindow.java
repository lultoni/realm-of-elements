import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StartWindow extends JFrame {
    private JComboBox<String> player1ComboBox;
    private JComboBox<String> player2ComboBox;
    JLabel songTitle = new JLabel();

    public StartWindow() {
        setTitle("Realm of Elements");
        Main.player.playRandomTrack();
        init();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);
    }

    private void init() {
        boolean opaque = false;

        BackgroundPanel outerPanel = new BackgroundPanel();
        outerPanel.setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(opaque);
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(opaque);
        JPanel middlePanel = new JPanel();
        middlePanel.setOpaque(opaque);
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(opaque);

        songTitle.setText("Song Name: " + Main.player.getTrackName());

        outerPanel.add(songTitle, BorderLayout.SOUTH);

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
        settingsButtonsPanel.setOpaque(opaque);
        JPanel tutorialButtonsPanel = new JPanel(new GridLayout(1, 0));
        tutorialButtonsPanel.setOpaque(opaque);
        JPanel exitButtonsPanel = new JPanel(new GridLayout(1, 0));
        exitButtonsPanel.setOpaque(opaque);
        JButton settingsButton = new JButton("Settings (WIP)");
        JButton tutorialButton = new JButton("Tutorial (WIP)");
        JButton exitButton = new JButton("Exit");

        settingsButton.addActionListener(e -> {
            // TODO add settings (add new player, change music volume, change sound volume, change timer)
            System.out.println("Settings button pressed.");
            repaint();
        });

        tutorialButton.addActionListener(e -> {
            // TODO add tutorial
            System.out.println("Tutorial button pressed.");
            repaint();
        });

        exitButton.addActionListener(e -> {
            System.out.println("Exit button pressed.");
            repaint();
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
                repaint();
            }
        });

        player2ComboBox.addActionListener(e -> {
            String selectedPlayerName = (String) player2ComboBox.getSelectedItem();
            Player player = getPlayerByName(selectedPlayerName);
            if (player != null) {
                player2Info.setText("ELO: " + player.elo + ", Games Played: " + player.gamesPlayed);
                player2Info.revalidate(); // Ensure the changes are reflected immediately
                repaint();
            }
        });

        Font font = new Font("Arial", Font.PLAIN, 35);
        player1ComboBox.setFont(font);
        player2ComboBox.setFont(font);
        settingsButton.setFont(font);
        exitButton.setFont(font);
        player1Info.setFont(font);
        player2Info.setFont(font);
        startGameButton.setFont(font);
        tutorialButton.setFont(font);
        songTitle.setFont(font);

        player1Info.setForeground(Color.WHITE);
        player2Info.setForeground(Color.WHITE);
        songTitle.setForeground(Color.WHITE);
        titleLabel.setForeground(Color.WHITE);

        settingsButtonsPanel.add(player1Info);
        settingsButtonsPanel.add(settingsButton);
        settingsButtonsPanel.add(player2Info);
        JPanel tp1 = new JPanel();
        JPanel tp2 = new JPanel();
        JPanel tp3 = new JPanel();
        JPanel tp4 = new JPanel();
        tp1.setOpaque(opaque);
        tp2.setOpaque(opaque);
        tp3.setOpaque(opaque);
        tp4.setOpaque(opaque);
        tutorialButtonsPanel.add(tp1); // Empty panel
        tutorialButtonsPanel.add(tutorialButton);
        tutorialButtonsPanel.add(tp2); // Empty panel
        exitButtonsPanel.add(tp3); // Empty panel
        exitButtonsPanel.add(exitButton);
        exitButtonsPanel.add(tp4); // Empty panel

        middlePanel.setLayout(new GridLayout(0, 1));
        middlePanel.add(titleLabel);
        middlePanel.add(playerPanel);
        middlePanel.add(settingsButtonsPanel);
        middlePanel.add(tutorialButtonsPanel);
        middlePanel.add(exitButtonsPanel);

        // Add panels to the main panel
        mainPanel.add(leftPanel, createGBC(1, 1, 0, 0));
        mainPanel.add(middlePanel, createGBC(3, 1, 1, 0));
        mainPanel.add(rightPanel, createGBC(1, 1, 4, 0));

        outerPanel.add(mainPanel);
        add(outerPanel);
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
                System.out.println("\nStarting Game");
                WAVPlayer.isPlaying = false;
                Main.player.skipNextTrack = true;
                Main.player.stopMusic();
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException b) {
                    b.printStackTrace();
                }
                dispose();
                WAVPlayer.isPlaying = true;
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

    public void updateSong() {
        System.out.println("UPDATING SONG IN GUI");
        songTitle.setText("Song Name: " + Main.player.getTrackName());
        repaint();
    }
}