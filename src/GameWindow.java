import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class GameWindow extends JFrame {

    private final GameHandler game;
    private final GridLayout outerLayout = new GridLayout(1, 0);
    private final GridLayout boardLayout = new GridLayout(8, 0);
    private final GridLayout controlOuterLayout = new GridLayout(3, 0);
    private final BorderLayout controlMiddleLayout = new BorderLayout();
    private final JPanel boardPanel = new JPanel();
    private final JPanel controlPanel = new JPanel();
    private final JPanel upperControlPanel = new JPanel();
    private final JPanel middleControlPanel = new JPanel();
    private final JPanel downControlPanel = new JPanel();
    private final JPanel spellPanel = new JPanel();
    private final JLabel player1tokens = new JLabel();
    private final JLabel player2tokens = new JLabel();
    private final JLabel roundCounter = new JLabel();
    private final JButton player1ActionButton = new JButton();
    private final JButton player2ActionButton = new JButton();

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
        updateText();
        upperControlPanel.add(player2tokens);
        player2ActionButton.addActionListener(e -> {
            if (game.turn == TurnState.P2MOVEMENT || game.turn == TurnState.P2ATTACK) {
                game.updateTurn();
            }
            updateText();
            repaint();
        });
        upperControlPanel.add(player2ActionButton);
        controlPanel.add(upperControlPanel);

        middleControlPanel.setLayout(controlMiddleLayout);
        middleControlPanel.add(roundCounter, BorderLayout.LINE_START);
        spellPanel.setLayout(controlOuterLayout);
        spellPanel.setBorder(BorderFactory.createTitledBorder("Spells:"));
        for (int i = 0; i < 15; i++) {
            String text = "Spell " + (i + 1);
            spellPanel.add(new JLabel(text));
        }
        middleControlPanel.add(spellPanel, BorderLayout.CENTER);
        controlPanel.add(middleControlPanel);

        downControlPanel.add(player1tokens);
        player1ActionButton.addActionListener(e -> {
            if (game.turn == TurnState.P1MOVEMENT || game.turn == TurnState.P1ATTACK) {
                game.updateTurn();
            }
            updateText();
            repaint();
        });
        downControlPanel.add(player1ActionButton);
        controlPanel.add(downControlPanel);

        add(boardPanel);
        add(controlPanel);
    }

    public void updateText() {
        Color moveCol = new Color(236, 184, 124);
        Color attCol = new Color(162, 117, 64);
        Color notCol = new Color(180, 93, 93);
        if (game.turn == TurnState.P2MOVEMENT) {
            player2ActionButton.setText("End Movement Phase");
            player2ActionButton.setBackground(moveCol);
            player1ActionButton.setText("Not your Turn");
            player1ActionButton.setBackground(notCol);
        } else if (game.turn == TurnState.P2ATTACK) {
            player2ActionButton.setText("End Attack Phase");
            player2ActionButton.setBackground(attCol);
            player1ActionButton.setText("Not your Turn");
            player1ActionButton.setBackground(notCol);
        } else if (game.turn == TurnState.P1MOVEMENT) {
            player2ActionButton.setText("Not your Turn");
            player2ActionButton.setBackground(notCol);
            player1ActionButton.setText("End Movement Phase");
            player1ActionButton.setBackground(moveCol);
        } else if (game.turn == TurnState.P1ATTACK) {
            player2ActionButton.setText("Not your Turn");
            player2ActionButton.setBackground(notCol);
            player1ActionButton.setText("End Attack Phase");
            player1ActionButton.setBackground(attCol);
        }
        player2tokens.setText("Player 2 Tokens: " + game.player2.spellTokens);
        roundCounter.setText("Round: " + game.round);
        player1tokens.setText("Player 1 Tokens: " + game.player1.spellTokens);
    }

}
