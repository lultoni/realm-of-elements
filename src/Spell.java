import javax.swing.*;
import java.awt.*;

public class Spell extends JPanel {

    GameHandler game;
    int cost;
    String name;
    String descriptionEffect;
    SpellType type;
    PieceType mageElement;
    private final GridBagLayout layout = new GridBagLayout();
    private final JLabel nameLabel = new JLabel();
    private final JLabel infoLabel = new JLabel();
    private final JPanel textPanel = new JPanel();
    private final JButton performSpell = new JButton();

    public Spell(GameHandler game) {
        this.game = game;
        init();
    }

    private void init() {
        // TODO give utility effects
        // TODO (show) spell path
        // TODO guards blocking spells

        setLayout(layout);
        textPanel.setLayout(layout);
        setBorder(BorderFactory.createEtchedBorder());

        nameLabel.setOpaque(true);
        infoLabel.setOpaque(true);

        updateText();

        nameLabel.setFont(new Font("Arial", Font.BOLD, 10));
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));


        performSpell.addActionListener(e -> {
            int mageCellId;
            if (!game.needsSpellCell && game.turn == TurnState.P1ATTACK || game.turn == TurnState.P2ATTACK) {
                switch (game.turn) {
                    case P1ATTACK -> {
                        for (Piece piece: game.player1.pieces) { // Go through every piece of player 1 (it's their turn)
                            if (piece.type == mageElement && piece.cellID != -1 && (game.hasTargetInRange(piece) || type != SpellType.OFFENSE)) { // is the piece of the correct element, and it has an enemy piece in its range
                                if (game.player1.spellTokens >= cost && game.player1.spellsLeft > 0) { // if they have enough spell tokens
                                    game.player1.spellTokens -= cost;
                                    game.player1.spellsLeft--;
                                    mageCellId = piece.cellID;
                                    game.activeSpell = this;
                                    game.spellCell = -1;
                                    game.spellFromID = mageCellId;
                                    game.needsSpellCell = true;
                                    if (type == SpellType.UTILITY && mageElement == PieceType.FIRE_MAGE) {
                                        game.spellCell2 = -1;
                                        game.needsSpellCell2 = true;
                                        game.spellCellCanBeEmpty = true;
                                    }
                                    System.out.println("I am activated :> " + name);
                                    game.window.updateText(false, false);
                                    break;
                                }
                                break;
                            }
                        }
                    }
                    case P2ATTACK -> {
                        for (Piece piece: game.player2.pieces) {
                            if (piece.type == mageElement && piece.cellID != -1 && (game.hasTargetInRange(piece) || type == SpellType.DEFENSE)) {
                                 if (game.player2.spellTokens >= cost && game.player2.spellsLeft > 0) {
                                     game.player2.spellTokens -= cost;
                                     game.player2.spellsLeft--;
                                     mageCellId = piece.cellID;
                                     game.activeSpell = this;
                                     game.spellCell = -1;
                                     game.spellFromID = mageCellId;
                                     game.needsSpellCell = true;
                                     System.out.println("I am activated :> " + name);
                                     game.window.updateText(false, false);
                                     break;
                                 }
                                break;
                            }
                        }
                    }
                }
            }
        });

        textPanel.add(nameLabel, createGBC(1, 1, 0, 0, 0.1F));
        textPanel.add(infoLabel, createGBC(1, 2, 0, 1, 1));
        add(textPanel, createGBC(1, 1, 0, 0, 1));
        add(performSpell, createGBC(1, 1, 0, 1, 1));
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
        Color isA = new Color(214, 239, 147);
        Color isnA = new Color(155, 155, 155);
        infoLabel.setBackground((game.activeSpell == this) ? isA : isnA);
        nameLabel.setBackground((game.activeSpell == this) ? isA : isnA);
    }

    private GridBagConstraints createGBC(int gridWidth, int gridHeight, int gridX, int gridY, float weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = weighty;
        gbc.weightx = 1;
        gbc.gridheight = gridHeight;
        gbc.gridwidth = gridWidth;
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }

}
