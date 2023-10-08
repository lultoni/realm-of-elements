import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    private GameHandler game;
    private final GridLayout outerLayout = new GridLayout(1, 0);
    private final GridLayout boardLayout = new GridLayout(8, 0);
    private final GridLayout controlOuterLayout = new GridLayout(3, 0);
    private final JPanel boardPanel = new JPanel();
    private final JPanel controlPanel = new JPanel();
    private final JPanel upperControlPanel = new JPanel();
    private final JPanel middleControlPanel = new JPanel();
    private final JPanel downControlPanel = new JPanel();
    private final JLabel player1tokens = new JLabel();
    private final JLabel player2tokens = new JLabel();
    private final JLabel roundCounter = new JLabel();

    public GameWindow(GameHandler game) {
        this.game = game;
        int width = 1000;
        setBounds(10, 10, width, (int) (width/1.85));
        setTitle("Realm of Elements");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
        setVisible(true);
    }

    private void init() {
        setLayout(outerLayout);

        boardPanel.setLayout(boardLayout);
        for (int i = 0; i < 64; i++) {
            boardPanel.add(game.board[i]);
        }

        controlPanel.setBackground(new Color(100, 100, 100));
        controlPanel.setLayout(controlOuterLayout);
        player2tokens.setText(String.valueOf(game.player2.spellTokens));
        upperControlPanel.add(player2tokens);
        controlPanel.add(upperControlPanel);
        roundCounter.setText(String.valueOf(game.round));
        middleControlPanel.add(roundCounter);
        controlPanel.add(middleControlPanel);
        player1tokens.setText(String.valueOf(game.player1.spellTokens));
        downControlPanel.add(player1tokens);
        controlPanel.add(downControlPanel);

        add(boardPanel);
        add(controlPanel);
    }

}
