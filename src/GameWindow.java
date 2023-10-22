import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameWindow extends JFrame {

    private final GameHandler game;
    private final GridLayout outerLayout = new GridLayout(1, 0);
    private final GridLayout boardLayout = new GridLayout(8, 0);
    private final GridBagLayout controlOuterLayout = new GridBagLayout();
    private final GridLayout spellPanelLayout = new GridLayout(4, 0);
    private final GridLayout controlUDLayout = new GridLayout(2, 0);
    private final BorderLayout controlMiddleLayout = new BorderLayout();
    private final JPanel outerBoardPanel = new JPanel();
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
    private final JPanel player1stPanel = new JPanel();
    private final JPanel player2stPanel = new JPanel();
    private final JLabel player1TI = new JLabel();
    private final JLabel player2TI = new JLabel();
    private final RoundWheel roundWheel = new RoundWheel();
    private final JButton player1ActionButton = new JButton();
    private final JButton player2ActionButton = new JButton();
    private final JLabel player1moves = new JLabel();
    private final JLabel player2moves = new JLabel();
    Spell fireAtt;
    Spell fireDef;
    Spell fireUti;
    Spell waterAtt;
    Spell waterDef;
    Spell waterUti;
    Spell earthAtt;
    Spell earthDef;
    Spell earthUti;
    Spell airAtt;
    Spell airDef;
    Spell airUti;
    Spell spiritAtt;
    Spell spiritDef;
    Spell spiritUti;
    JLabel fire;
    JLabel water;
    JLabel earth;
    JLabel air;
    JLabel spirit;
    Image fireImage;
    Image waterImage;
    Image earthImage;
    Image airImage;
    Image spiritImage;
    EvaluationBar evaluationBar;

    public GameWindow(GameHandler game) {
        this.game = game;
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Realm of Elements");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
        game.window = this;
        setVisible(true);
    }

    private void init() {
        setLayout(outerLayout);

        ImageIcon icon = new ImageIcon("RoE_Icon.png");
        setIconImage(icon.getImage());

        Font spellFont = new Font("Arial", Font.BOLD, 30);
        Font moveFont = new Font("Arial", Font.BOLD, 20);
        Color colorTokens = new Color(234, 194, 28);

        player1tokens.setForeground(colorTokens);
        player1tokens.setFont(spellFont);
        player2tokens.setForeground(colorTokens);
        player2tokens.setFont(spellFont);
        player1moves.setFont(moveFont);
        player2moves.setFont(moveFont);

        evaluationBar = new EvaluationBar(game.evaluate());
        boardPanel.setLayout(boardLayout);
        for (int i = 0; i < 64; i++) {
            boardPanel.add(game.board[i]);
            game.board[i].addActionListener(e -> updateText(false));
        }
        JPanel outerBoardNorthPanel = new JPanel();
        outerBoardNorthPanel.setLayout(new BorderLayout());

        JLabel p1 = new JLabel();
        p1.setText("  " + ((game.player1.name == null) ? "Player 1" : game.player1.name) + "(" + ((game.player1.elo == 0) ? "?" : game.player1.elo) + ") (" + ((game.player1.gamesPlayed == 0) ? "?" : game.player1.gamesPlayed) + ")");
        JLabel p2 = new JLabel();
        p2.setText("  " + ((game.player2.name == null) ? "Player 2" : game.player2.name) + "(" + ((game.player2.elo == 0) ? "?" : game.player2.elo) + ") (" + ((game.player2.gamesPlayed == 0) ? "?" : game.player2.gamesPlayed) + ")");
        int size = 40;
        p1.setFont(new Font("Arial", Font.PLAIN, size));
        p2.setFont(new Font("Arial", Font.PLAIN, size));

        JButton settingsMenu = new JButton("Settings");

        // Create a popup menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem countAsDrawItem = new JMenuItem("Count as Draw");
        JMenuItem restartGameItem = new JMenuItem("Restart Game");

        // Add action listeners to the menu items
        countAsDrawItem.addActionListener(e -> {
            // Implement the logic for counting the game as a draw
            System.out.println("Both Players accept a draw.");
        });

        restartGameItem.addActionListener(e -> {
            // Implement the logic for restarting the game
            System.out.println("Restarting the game.");
        });

        // Add menu items to the popup menu
        popupMenu.add(countAsDrawItem);
        popupMenu.add(restartGameItem);

        // Add an action listener to the "Settings" button to show the popup menu
        settingsMenu.addActionListener(e -> {
            popupMenu.show(settingsMenu, 0, settingsMenu.getHeight());
        });

        outerBoardNorthPanel.add(p2, BorderLayout.CENTER);
        outerBoardNorthPanel.add(settingsMenu, BorderLayout.WEST);

        outerBoardPanel.setLayout(new BorderLayout());
        outerBoardPanel.add(outerBoardNorthPanel, BorderLayout.NORTH);
        outerBoardPanel.add(evaluationBar, BorderLayout.WEST);
        outerBoardPanel.add(boardPanel, BorderLayout.CENTER);
        outerBoardPanel.add(p1, BorderLayout.SOUTH);

        // Round Wheel Complementary Colors
        // Dark Blueish #3F6172
        // Light Brown #936751
        // Army #726A3F
        // Reddish Brown #723F48
        Color background = new Color(96, 90, 90);
        p1.setBackground(background);
        p2.setBackground(background);
        outerBoardNorthPanel.setBackground(background);
        outerBoardPanel.setBackground(background);
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
        player1TI.setBackground(background);
        player2TI.setBackground(background);
        player1stPanel.setBackground(background);
        player2stPanel.setBackground(background);

        player1stPanel.setLayout(controlOuterLayout);
        player2stPanel.setLayout(controlOuterLayout);

        controlPanel.setLayout(controlOuterLayout);
        player2captures.pieces = game.player1.pieces;
        player1captures.pieces = game.player2.pieces;
        upperControlPanel.setLayout(controlUDLayout);
        upperBufferPanel.setLayout(outerLayout);
        Image st = new ImageIcon("SpellToken.png").getImage();
        player2TI.setIcon(new ImageIcon(st.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        player2TI.setOpaque(false);
        player2stPanel.add(player2TI, createGBC(1, 1, 0, 0));
        player2stPanel.add(player2tokens, createGBC(2, 1, 1, 0));
        JPanel tempPan = new JPanel();
        tempPan.setBackground(background);
        JPanel tempPan2 = new JPanel();
        tempPan2.setBackground(background);
        player2stPanel.add(tempPan2, createGBC(1, 1, 3, 0));
        upperBufferPanel.add(player2stPanel);
        upperBufferPanel.add(player2moves);
        player2ActionButton.addActionListener(e -> {
            if (game.gameOver) return;
            if (game.turn == TurnState.P2MOVEMENT || game.turn == TurnState.P2ATTACK) {
                game.updateTurn();
            }
            updateText(false);
            repaint();
        });
        upperBufferPanel.add(player2ActionButton);
        upperControlPanel.add(upperBufferPanel);
        upperControlPanel.add(player2captures);
        controlPanel.add(upperControlPanel, createGBC(1, 1, 0, 0));

        middleControlPanel.setLayout(controlMiddleLayout);
        middleControlPanel.add(roundWheel, BorderLayout.LINE_START);
        spellPanel.setLayout(spellPanelLayout);
        TitledBorder border = BorderFactory.createTitledBorder("Spells: (" + game.getCurrentPlayer().spellsLeft + ")");
        border.setTitleColor(Color.WHITE);
        border.setTitleFont(new Font("Arial", Font.PLAIN, 20));
        spellPanel.setBorder(border);

        fireAtt = new Spell(game);
        fireAtt.name = "Fireball";
        fireAtt.descriptionEffect = "Kill the target, give space Inferno<br>effect for 1 turn.";
        fireAtt.cost = 3;
        fireAtt.updateText();
        fireDef = new Spell(game);
        fireDef.name = "Blazing Barrier";
        fireDef.descriptionEffect = "Immune to attacks (not spells) for one turn.";
        fireDef.cost = 2;
        fireDef.updateText();
        fireUti = new Spell(game);
        fireUti.name = "Inferno";
        fireUti.descriptionEffect = "3 by 1 Area in which every piece dies that passes through, can’t be cast on full squares, lasts for two turns.";
        fireUti.cost = 4;
        fireUti.updateText();
        waterAtt = new Spell(game);
        waterAtt.name = "Icy Spear";
        waterAtt.descriptionEffect = "Kill the target.";
        waterAtt.cost = 2;
        waterAtt.updateText();
        waterDef = new Spell(game);
        waterDef.name = "Aqua Shield";
        waterDef.descriptionEffect = "Immune to spells for one turn.";
        waterDef.cost = 2;
        waterDef.updateText();
        waterUti = new Spell(game);
        waterUti.name = "Tidal Surge";
        waterUti.descriptionEffect = "Push back all pieces from range plus 2 by two spaces.";
        waterUti.cost = 4;
        waterUti.updateText();
        earthAtt = new Spell(game);
        earthAtt.name = "Rock-slide";
        earthAtt.descriptionEffect = "Kill the target and make space unavailable for two turns.";
        earthAtt.cost = 3;
        earthAtt.updateText();
        earthDef = new Spell(game);
        earthDef.name = "Stone Wall";
        earthDef.descriptionEffect = "Immune to spells for one turn.";
        earthDef.cost = 2;
        earthDef.updateText();
        earthUti = new Spell(game);
        earthUti.name = "Tremor";
        earthUti.descriptionEffect = "2 by 2 area make pieces skip turn.";
        earthUti.cost = 4;
        earthUti.updateText();
        airAtt = new Spell(game);
        airAtt.name = "Gale Force";
        airAtt.descriptionEffect = "Kill the target and push back surrounding pieces by one.";
        airAtt.cost = 3;
        airAtt.updateText();
        airDef = new Spell(game);
        airDef.name = "Areal Shield";
        airDef.descriptionEffect = "Apply spell cast on mage on attacker as well, lasts for one turn.";
        airDef.cost = 2;
        airDef.updateText();
        airUti = new Spell(game);
        airUti.name = "Zephyr Step";
        airUti.descriptionEffect = "Move to an adjacent tile.";
        airUti.cost = 3;
        airUti.updateText();
        spiritAtt = new Spell(game);
        spiritAtt.name = "Soul Siphon";
        spiritAtt.descriptionEffect = "Kill the target and take one spell token from the opponent if they have at least 1 token.";
        spiritAtt.cost = 3;
        spiritAtt.updateText();
        spiritDef = new Spell(game);
        spiritDef.name = "Ethereal Shield";
        spiritDef.descriptionEffect = "Immune to spells for one turn.";
        spiritDef.cost = 2;
        spiritDef.updateText();
        spiritUti = new Spell(game);
        spiritUti.name = "Soul Swap";
        spiritUti.descriptionEffect = "Switch two of your own pieces.";
        spiritUti.cost = 4;
        spiritUti.updateText();

        boolean isBlue = game.turn == TurnState.P1MOVEMENT || game.turn == TurnState.P1ATTACK;

        ImageIcon fireIcon = new ImageIcon(((isBlue) ? "Blue" : "Red") + "FireMage.png");
        ImageIcon waterIcon = new ImageIcon(((isBlue) ? "Blue" : "Red") + "WaterMage.png");
        ImageIcon earthIcon = new ImageIcon(((isBlue) ? "Blue" : "Red") + "EarthMage.png");
        ImageIcon airIcon = new ImageIcon(((isBlue) ? "Blue" : "Red") + "AirMage.png");
        ImageIcon spiritIcon = new ImageIcon(((isBlue) ? "Blue" : "Red") + "SpiritMage.png");

        fireImage = fireIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        waterImage = waterIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        earthImage = earthIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        airImage = airIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        spiritImage = spiritIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);

        fire = new JLabel(new ImageIcon(fireImage));
        water = new JLabel(new ImageIcon(waterImage));
        earth = new JLabel(new ImageIcon(earthImage));
        air = new JLabel(new ImageIcon(airImage));
        spirit = new JLabel(new ImageIcon(spiritImage));

        spellPanel.add(fire);
        spellPanel.add(water);
        spellPanel.add(earth);
        spellPanel.add(air);
        spellPanel.add(spirit);


        fireAtt.type = SpellType.OFFENSE;
        fireAtt.mageElement = PieceType.FIRE_MAGE;
        spellPanel.add(fireAtt);
        waterAtt.type = SpellType.OFFENSE;
        waterAtt.mageElement = PieceType.WATER_MAGE;
        spellPanel.add(waterAtt);
        earthAtt.type = SpellType.OFFENSE;
        earthAtt.mageElement = PieceType.EARTH_MAGE;
        spellPanel.add(earthAtt);
        airAtt.type = SpellType.OFFENSE;
        airAtt.mageElement = PieceType.AIR_MAGE;
        spellPanel.add(airAtt);
        spiritAtt.type = SpellType.OFFENSE;
        spiritAtt.mageElement = PieceType.SPIRIT_MAGE;
        spellPanel.add(spiritAtt);

        fireDef.type = SpellType.DEFENSE;
        fireDef.mageElement = PieceType.FIRE_MAGE;
        spellPanel.add(fireDef);
        waterDef.type = SpellType.DEFENSE;
        waterDef.mageElement = PieceType.WATER_MAGE;
        spellPanel.add(waterDef);
        earthDef.type = SpellType.DEFENSE;
        earthDef.mageElement = PieceType.EARTH_MAGE;
        spellPanel.add(earthDef);
        airDef.type = SpellType.DEFENSE;
        airDef.mageElement = PieceType.AIR_MAGE;
        spellPanel.add(airDef);
        spiritDef.type = SpellType.DEFENSE;
        spiritDef.mageElement = PieceType.SPIRIT_MAGE;
        spellPanel.add(spiritDef);

        fireUti.type = SpellType.UTILITY;
        fireUti.mageElement = PieceType.FIRE_MAGE;
        spellPanel.add(fireUti);
        waterUti.type = SpellType.UTILITY;
        waterUti.mageElement = PieceType.WATER_MAGE;
        spellPanel.add(waterUti);
        earthUti.type = SpellType.UTILITY;
        earthUti.mageElement = PieceType.EARTH_MAGE;
        spellPanel.add(earthUti);
        airUti.type = SpellType.UTILITY;
        airUti.mageElement = PieceType.AIR_MAGE;
        spellPanel.add(airUti);
        spiritUti.type = SpellType.UTILITY;
        spiritUti.mageElement = PieceType.SPIRIT_MAGE;
        spellPanel.add(spiritUti);

        middleControlPanel.add(spellPanel, BorderLayout.CENTER);
        controlPanel.add(middleControlPanel, createGBC(1, 2, 0, 1));

        downControlPanel.setLayout(controlUDLayout);
        downBufferPanel.setLayout(outerLayout);
        player1TI.setIcon(new ImageIcon(st.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        player1TI.setOpaque(false);
        player1stPanel.add(player1TI, createGBC(1, 1, 0, 0));
        player1stPanel.add(player1tokens, createGBC(2, 1, 1, 0));
        player1stPanel.add(tempPan, createGBC(1, 1, 3, 0));
        downBufferPanel.add(player1stPanel);
        downBufferPanel.add(player1moves);
        player1ActionButton.addActionListener(e -> {
            if (game.gameOver) return;
            if (game.turn == TurnState.P1MOVEMENT || game.turn == TurnState.P1ATTACK) {
                game.updateTurn();
            }
            updateText(false);
            repaint();
        });
        downBufferPanel.add(player1ActionButton);
        downControlPanel.add(player1captures);
        downControlPanel.add(downBufferPanel);
        controlPanel.add(downControlPanel, createGBC(1, 1, 0, 3));

        updateText(true);

        add(outerBoardPanel);
        add(controlPanel);
    }

    public void updateText(boolean isFirstTurn) {
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
        if (game.selectedPiece != null) {
            game.board[game.selectedPiece.cellID].setBorder(new LineBorder(Color.BLACK, 5));
        } else if (game.oldSelID != -1) {
            game.board[game.oldSelID].setBorder(UIManager.getBorder("Button.border"));
            game.oldSelID = -1;
        }
        TitledBorder border = BorderFactory.createTitledBorder("Spells: (" + game.getCurrentPlayer().spellsLeft + ")");
        border.setTitleColor(Color.WHITE);
        border.setTitleFont(new Font("Arial", Font.PLAIN, 20));
        spellPanel.setBorder(border);
        player1captures.updateCaptures();
        player2captures.updateCaptures();
        player2tokens.setText(game.player2.spellTokens + " (+" + game.tokenChange + ")");
        if (game.turn == TurnState.P2ATTACK) {
            player2moves.setForeground((game.player2.hasAttacked) ? notCol : attCol);
            player2moves.setText("CanAttack: " + ((game.player2.hasAttacked) ? "No" : "Yes"));
        } else {
            player2moves.setForeground((game.player2.movementCounter == 0) ? notCol : moveCol);
            player2moves.setText("Moves: " + game.player2.movementCounter + "/3");
        }
        roundWheel.setRound(game.round);
        roundWheel.updateText();
        player1tokens.setText(game.player1.spellTokens + " (+" + game.tokenChange + ")");
        if (game.turn == TurnState.P1ATTACK) {
            player1moves.setForeground((game.player1.hasAttacked) ? notCol : attCol);
            player1moves.setText("CanAttack: " + ((game.player1.hasAttacked) ? "No" : "Yes"));
        } else {
            player1moves.setForeground((game.player1.movementCounter == 0) ? notCol : moveCol);
            player1moves.setText("Moves: " + game.player1.movementCounter + "/3");
        }
        boolean isBlue = game.isP1Turn();
        ImageIcon fireIcon = new ImageIcon(((isBlue) ? "Blue" : "Red") + "FireMage.png");
        ImageIcon waterIcon = new ImageIcon(((isBlue) ? "Blue" : "Red") + "WaterMage.png");
        ImageIcon earthIcon = new ImageIcon(((isBlue) ? "Blue" : "Red") + "EarthMage.png");
        ImageIcon airIcon = new ImageIcon(((isBlue) ? "Blue" : "Red") + "AirMage.png");
        ImageIcon spiritIcon = new ImageIcon(((isBlue) ? "Blue" : "Red") + "SpiritMage.png");
        int size = 60;
        fireImage = fireIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        waterImage = waterIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        earthImage = earthIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        airImage = airIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        spiritImage = spiritIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        try {
            fire.setIcon(new ImageIcon(fireImage));
            water.setIcon(new ImageIcon(waterImage));
            earth.setIcon(new ImageIcon(earthImage));
            air.setIcon(new ImageIcon(airImage));
            spirit.setIcon(new ImageIcon(spiritImage));
            fireAtt.updateText();
            fireDef.updateText();
            fireUti.updateText();
            waterAtt.updateText();
            waterDef.updateText();
            waterUti.updateText();
            earthAtt.updateText();
            earthDef.updateText();
            earthUti.updateText();
            airAtt.updateText();
            airDef.updateText();
            airUti.updateText();
            spiritAtt.updateText();
            spiritDef.updateText();
            spiritUti.updateText();
        } catch (NullPointerException use) {
            System.out.println("spell update errors");
        }
        if (!isFirstTurn) evaluationBar.shouldAnimate = true;
        evaluationBar.setEvaluation(game.evaluate());
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

}
