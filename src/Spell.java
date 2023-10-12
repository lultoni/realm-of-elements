import javax.swing.*;
import java.awt.*;

public class Spell extends JPanel {

    GameHandler game;
    int cost;
    String name;
    String descriptionEffect;
    SpellType type;
    PieceType mageElement;
    GridLayout layout = new GridLayout(0, 1);
    JLabel nameLabel = new JLabel();
    JLabel infoLabel = new JLabel();
    JButton performSpell = new JButton();

    public Spell(GameHandler game) {
        this.game = game;
        init();
    }

    private void init() {
        // TODO give effects
        // TODO spell range
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
            switch (game.turn) {
                case P1ATTACK, P1MOVEMENT -> {
                    for (Piece piece: game.player1.pieces) {
                        if (piece.type == mageElement && piece.cellID != -1) {
                            game.player1.spellTokens -= (game.player1.spellTokens >= cost) ? cost : 0;
                        }
                    }
                }
                case P2ATTACK, P2MOVEMENT -> {
                    for (Piece piece: game.player2.pieces) {
                        if (piece.type == mageElement && piece.cellID != -1) {
                            game.player2.spellTokens -= (game.player2.spellTokens >= cost) ? cost : 0;
                        }
                    }
                }
            }
            game.window.updateText(false, false);
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
                    }
                }
            }
            case P2ATTACK, P2MOVEMENT -> {
                for (Piece piece: game.player2.pieces) {
                    if (piece.type == mageElement && piece.cellID != -1) {
                        canPerform = game.player2.spellTokens >= cost;
                    }
                }
            }
        }
        Color can = new Color(154, 213, 239);
        Color cant = new Color(224, 133, 133);
        performSpell.setBackground((canPerform) ? can : cant);
    }

    public void effect() {

    }

}
