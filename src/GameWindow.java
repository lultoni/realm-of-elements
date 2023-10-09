import javax.swing.*;
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
    private final JLabel player1moves = new JLabel();
    private final JLabel player2moves = new JLabel();

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
            game.board[i].addActionListener(e -> updateText(game.selectedPiece != null, game.turn == TurnState.P1MOVEMENT || game.turn == TurnState.P1ATTACK));
        }

        controlPanel.setLayout(controlOuterLayout);
        updateText(false, false);
        upperControlPanel.setLayout(outerLayout);
        upperControlPanel.add(player2tokens);
        upperControlPanel.add(player2moves);
        player2ActionButton.addActionListener(e -> {
            if (game.turn == TurnState.P2MOVEMENT || game.turn == TurnState.P2ATTACK) {
                game.updateTurn();
            }
            updateText(false, false);
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

        downControlPanel.setLayout(outerLayout);
        downControlPanel.add(player1tokens);
        downControlPanel.add(player1moves);
        player1ActionButton.addActionListener(e -> {
            if (game.turn == TurnState.P1MOVEMENT || game.turn == TurnState.P1ATTACK) {
                game.updateTurn();
            }
            updateText(false, false);
            repaint();
        });
        downControlPanel.add(player1ActionButton);
        controlPanel.add(downControlPanel);

        add(boardPanel);
        add(controlPanel);
    }

    public void updateText(boolean preDown, boolean turnBlue) {
        Color moveCol = new Color(71, 167, 213);
        Color attCol = new Color(210, 130, 44);
        Color notCol = new Color(166, 85, 85);
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
        player2tokens.setText("P2 SpellTokens: " + game.player2.spellTokens + " (+" + game.tokenChange + ")");
        player2moves.setText("P2 MovementTokens: " + (game.player2.movementCounter + ((preDown && !turnBlue) ? -1 : 0)));
        roundCounter.setText("Round: " + game.round);
        player1tokens.setText("P1 SpellTokens: " + game.player1.spellTokens + " (+" + game.tokenChange + ")");
        player1moves.setText("P1 MovementTokens: " + (game.player1.movementCounter + ((preDown && turnBlue) ? -1 : 0)));
    }

}
