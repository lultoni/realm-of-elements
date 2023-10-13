import javax.swing.*;
import java.awt.*;

public class Spell extends JPanel {

    GameHandler game;
    int cost;
    String name;
    String descriptionEffect;
    SpellType type;
    PieceType mageElement;
    private final GridLayout layout = new GridLayout(0, 1);
    private final JLabel nameLabel = new JLabel();
    private final JLabel infoLabel = new JLabel();
    private final JButton performSpell = new JButton();

    public Spell(GameHandler game) {
        this.game = game;
        init();
    }

    private void init() {
        // TODO give effects
        // TODO (show) spell path
        // TODO spell cast limit (and increase + showing it)
        // TODO guards blocking spells
        // TODO protection logic and showing in window
        // TODO who can cast spells / from where will the spell be cast
        // TODO show spell effects in the terrain

        setLayout(layout);
        setBorder(BorderFactory.createEtchedBorder());

        updateText();

        nameLabel.setFont(new Font("Arial", Font.BOLD, 10));
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));


        performSpell.addActionListener(e -> {
            int mageCellId = -1;
            if (!game.needsSpellCell && game.turn == TurnState.P1ATTACK || game.turn == TurnState.P2ATTACK) {
                switch (game.turn) {
                    case P1ATTACK -> {
                        for (Piece piece: game.player1.pieces) { // Go through every piece of player 1 (it's their turn)
                            if (piece.type == mageElement && piece.cellID != -1 && game.hasTargetInRange(piece)) { // is the piece of the correct element, and it has an enemy piece in its range
                                if (game.player1.spellTokens >= cost) { // if they have enough spell tokens
                                    game.player1.spellTokens -= cost;
                                    mageCellId = piece.cellID;
                                    break;
                                }
                                break;
                            }
                        }
                    }
                    case P2ATTACK -> {
                        for (Piece piece: game.player2.pieces) {
                            if (piece.type == mageElement && piece.cellID != -1 && game.hasTargetInRange(piece)) {
                                 if (game.player2.spellTokens >= cost) {
                                     game.player2.spellTokens -= cost;
                                     mageCellId = piece.cellID;
                                     break;
                                 }
                                break;
                            }
                        }
                    }
                }
                game.activeSpell = this;
                game.spellCell = -1;
                game.spellFromID = mageCellId;
                game.needsSpellCell = true;
                System.out.println("I am activated :> " + name);
                game.window.updateText(false, false);
            }
        });

        add(nameLabel);
        add(infoLabel);
        add(performSpell);
    }

    public void updateText() {
        nameLabel.setText(name);
        infoLabel.setText(descriptionEffect);
        performSpell.setText(String.valueOf(cost));

        boolean canPerform = false;
        switch (game.turn) {
            case P1ATTACK, P1MOVEMENT -> {
                for (Piece piece: game.player1.pieces) {
                    if (piece.type == mageElement && piece.cellID != -1) {
                        canPerform = game.player1.spellTokens >= cost;
                        break;
                    }
                }
            }
            case P2ATTACK, P2MOVEMENT -> {
                for (Piece piece: game.player2.pieces) {
                    if (piece.type == mageElement && piece.cellID != -1) {
                        canPerform = game.player2.spellTokens >= cost;
                        break;
                    }
                }
            }
        }
        Color can = new Color(154, 213, 239);
        Color cant = new Color(224, 133, 133);
        performSpell.setBackground((canPerform) ? can : cant);
    }

}
