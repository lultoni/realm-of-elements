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

        setLayout(layout);
        textPanel.setLayout(layout);
        setBorder(BorderFactory.createEtchedBorder());

        nameLabel.setOpaque(true);
        infoLabel.setOpaque(true);

        updateText();

        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        performSpell.setFont(new Font("Arial", Font.PLAIN, 20));


        performSpell.addActionListener(e -> {
            System.out.println("Spell " + name + " clicked");
            int mageCellId;
            if (game.activeSpell != null) {
                System.out.println("Canceling Active Spell");
                game.getCurrentPlayer().spellTokens += game.activeSpell.cost;
                game.getCurrentPlayer().spellsLeft++;
                game.activeSpell = null;
                game.spellCell = -1;
                game.spellFromID = -1;
                game.needsSpellCell = false;
                game.spellCell2 = -1;
                game.needsSpellCell2 = false;
                game.spellCellCanBeEmpty = false;
                game.window.updateText(false);
                WAVPlayer.play("SpellCancel.wav");
                return;
            }
            if (!game.needsSpellCell && (game.isP1Attack() || game.isP2Attack())) {
                System.out.println("Spell: Not needing SC, Any Attack Turn");
                Player player = game.getCurrentPlayer();
                for (Piece piece: player.pieces) { // Go through every piece of player
                    if (piece.type == mageElement && piece.cellID != -1 && (game.hasTargetInRange(piece) || type != SpellType.OFFENSE)) { // is the piece of the correct element, and it has an enemy piece in its range
                        System.out.println("Spell: Correct Element && hasTarget or not offense");
                        if (player.spellTokens >= cost && player.spellsLeft > 0 && !piece.isSkippingTurn) { // if they have enough spell tokens
                            System.out.println("Spell: Player can afford");
                            player.spellTokens -= cost;
                            player.spellsLeft--;
                            mageCellId = piece.cellID;
                            game.activeSpell = this;
                            game.spellCell = -1;
                            game.spellFromID = mageCellId;
                            game.needsSpellCell = true;
                            if (type == SpellType.UTILITY && (mageElement == PieceType.FIRE_MAGE || mageElement == PieceType.EARTH_MAGE || mageElement == PieceType.SPIRIT_MAGE)) {
                                System.out.println("SC can be empty & need 2");
                                game.spellCell2 = -1;
                                game.needsSpellCell2 = true;
                                game.spellCellCanBeEmpty = true;
                            } else if (type == SpellType.UTILITY && mageElement == PieceType.AIR_MAGE) {
                                System.out.println("SC can be empty");
                                game.spellCellCanBeEmpty = true;
                            }
                            System.out.println("I am activated :> " + name);
                            WAVPlayer.play("SpellActivation.wav");
                            game.window.updateText(false);
                            break;
                        }
                        break;
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
        Color isA = new Color(214, 239, 147);
        Color isnA = new Color(155, 155, 155);
        Color cancel = new Color(252, 209, 128);
        performSpell.setBackground((game.activeSpell != null) ? cancel : (canPerform) ? can : cant);
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
