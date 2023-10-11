import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    private final GameHandler game;
    private final GridLayout outerLayout = new GridLayout(1, 0);
    private final GridLayout boardLayout = new GridLayout(8, 0);
    private final GridLayout controlOuterLayout = new GridLayout(3, 0);
    private final GridLayout controlUDLayout = new GridLayout(2, 0);
    private final BorderLayout controlMiddleLayout = new BorderLayout();
    private final JPanel boardPanel = new JPanel();
    private final JPanel controlPanel = new JPanel();
    private final JPanel upperControlPanel = new JPanel();
    private final JPanel middleControlPanel = new JPanel();
    private final JPanel downControlPanel = new JPanel();
    private final JPanel spellPanel = new JPanel();
    private final JPanel upperBufferPanel = new JPanel();
    private final JPanel downBufferPanel = new JPanel();
    private final PieceDisplay player1captures = new PieceDisplay();
    private final PieceDisplay player2captures = new PieceDisplay();
    private final JLabel player1tokens = new JLabel();
    private final JLabel player2tokens = new JLabel();
    private final RoundWheel roundWheel = new RoundWheel();
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
        game.window = this;
        setVisible(true);
    }

    private void init() {
        setLayout(outerLayout);

        player1tokens.setForeground(Color.WHITE);
        player2tokens.setForeground(Color.WHITE);
        player1moves.setForeground(Color.WHITE);
        player2moves.setForeground(Color.WHITE);

        boardPanel.setLayout(boardLayout);
        for (int i = 0; i < 64; i++) {
            boardPanel.add(game.board[i]);
            game.board[i].addActionListener(e -> updateText(game.selectedPiece != null, game.turn == TurnState.P1MOVEMENT || game.turn == TurnState.P1ATTACK));
        }

        // Round Wheel Complementary Colors
        // Dark Blueish #3F6172
        // Light Brown #936751
        // Army #726A3F
        // Reddish Brown #723F48
        Color background = new Color(76, 76, 76);
        boardPanel.setBackground(background);
        controlPanel.setBackground(background);
        upperControlPanel.setBackground(background);
        middleControlPanel.setBackground(background);
        downControlPanel.setBackground(background);
        spellPanel.setBackground(background);
        upperBufferPanel.setBackground(background);
        downBufferPanel.setBackground(background);
        player1captures.setBackground(background);
        player2captures.setBackground(background);

        controlPanel.setLayout(controlOuterLayout);
        player2captures.pieces = game.player1.pieces;
        player1captures.pieces = game.player2.pieces;
        updateText(false, false);
        upperControlPanel.setLayout(controlUDLayout);
        upperBufferPanel.setLayout(outerLayout);
        upperBufferPanel.add(player2tokens);
        upperBufferPanel.add(player2moves);
        player2ActionButton.addActionListener(e -> {
            if (game.turn == TurnState.P2MOVEMENT || game.turn == TurnState.P2ATTACK) {
                game.updateTurn();
            }
            updateText(false, false);
            repaint();
        });
        upperBufferPanel.add(player2ActionButton);
        upperControlPanel.add(upperBufferPanel);
        upperControlPanel.add(player2captures);
        controlPanel.add(upperControlPanel);

        middleControlPanel.setLayout(controlMiddleLayout);
        middleControlPanel.add(roundWheel, BorderLayout.LINE_START);
        spellPanel.setLayout(controlOuterLayout);
        spellPanel.setBorder(BorderFactory.createTitledBorder("Spells:"));
        for (int i = 0; i < 15; i++) {
            String text = "Spell " + (i + 1);
            spellPanel.add(new JLabel(text));
        }
        middleControlPanel.add(spellPanel, BorderLayout.CENTER);
        controlPanel.add(middleControlPanel);

        downControlPanel.setLayout(controlUDLayout);
        downBufferPanel.setLayout(outerLayout);
        downBufferPanel.add(player1tokens);
        downBufferPanel.add(player1moves);
        player1ActionButton.addActionListener(e -> {
            if (game.turn == TurnState.P1MOVEMENT || game.turn == TurnState.P1ATTACK) {
                game.updateTurn();
            }
            updateText(false, false);
            repaint();
        });
        downBufferPanel.add(player1ActionButton);
        downControlPanel.add(player1captures);
        downControlPanel.add(downBufferPanel);
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
        player1captures.updateCaptures();
        player2captures.updateCaptures();
        player2tokens.setText("P2 SpellTokens: " + game.player2.spellTokens + " (+" + game.tokenChange + ")");
        if (game.turn == TurnState.P2ATTACK) {
            player2moves.setText("P2 CanAttack: " + ((game.player2.hasAttacked) ? "No" : "Yes"));
        } else {
            player2moves.setText("P2 MovementTokens: " + (game.player2.movementCounter + ((preDown && !turnBlue) ? -1 : 0)));
        }
        roundWheel.setRound(game.round);
        roundWheel.updateText();
        player1tokens.setText("P1 SpellTokens: " + game.player1.spellTokens + " (+" + game.tokenChange + ")");
        if (game.turn == TurnState.P1ATTACK) {
            player1moves.setText("P1 CanAttack: " + ((game.player1.hasAttacked) ? "No" : "Yes"));
        } else {
            player1moves.setText("P1 MovementTokens: " + (game.player1.movementCounter + ((preDown && turnBlue) ? -1 : 0)));
        }
    }

}
