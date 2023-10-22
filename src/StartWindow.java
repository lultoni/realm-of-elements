import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StartWindow extends JFrame {
    private JButton startGameButton;
    private JComboBox<String> player1ComboBox;
    private JComboBox<String> player2ComboBox;

    public StartWindow() {
        setTitle("Realm of Elements");
        init();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void init() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel titleLabel = new JLabel("Realm of Elements");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        panel.add(titleLabel);
        panel.add(new JLabel(""));

        ArrayList<String> players = new ArrayList<>();
        for (Player player: DBH.getAllPlayers()) {
            players.add(player.name);
        }

        JLabel player1Label = new JLabel("Player 1:");
        panel.add(player1Label);
        player1ComboBox = new JComboBox<>(players.toArray(new String[0]));
        panel.add(player1ComboBox);

        JLabel player2Label = new JLabel("Player 2:");
        panel.add(player2Label);
        player2ComboBox = new JComboBox<>(players.toArray(new String[0]));
        panel.add(player2ComboBox);


        startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(e -> {
            String selectedPlayer1 = (String) player1ComboBox.getSelectedItem();
            String selectedPlayer2 = (String) player2ComboBox.getSelectedItem();

            if (!selectedPlayer1.equals(selectedPlayer2)) {
                Main.isStartReady = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(StartWindow.this, "Please select different players for both sides.");
            }
        });
        panel.add(startGameButton);

        add(panel);
    }

    public Player getPlayer1() {
        ArrayList<Player> allPlayers = DBH.getAllPlayers();
        for (Player player: allPlayers) {
            if (player.name.equals(player1ComboBox.getSelectedItem())) return player; // TODO check if the equals works
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

}
