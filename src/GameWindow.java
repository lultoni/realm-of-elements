import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

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
    private final JPanel player1capTimer = new JPanel();
    private final JPanel player2capTimer = new JPanel();
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
    Color background = new Color(96, 90, 90);
    private final JPanel fullPanel = new JPanel();
    JLabel songTitle = new JLabel();
    Timer player1timer;
    Timer player2timer;

    public GameWindow(GameHandler game) {
        this.game = game;
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setTitle("Realm of Elements");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
        game.window = this;
        setVisible(true);
    }

    private void init() {
        setBackground(background);
        fullPanel.setLayout(outerLayout);
        setLayout(new BorderLayout());

        player1timer = new Timer(game.player1, 60, 30, game);
        player1timer.name = "P1 Timer: ";
        player2timer = new Timer(game.player2, 60, 30, game);
        player2timer.name = "P2 Timer: ";

        ImageIcon icon = new ImageIcon("RoE_Icon.png");
        setIconImage(icon.getImage());

        Font spellFont = new Font("Arial", Font.BOLD, 30);
        Font moveFont = new Font("Arial", Font.BOLD, 40);
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

        String text;
        int size = 40;

        JPanel outerBoardNorthPanel = new JPanel();
        outerBoardNorthPanel.setLayout(new BorderLayout());

        JButton settingsMenu = getSettingsMenu();
        JLabel p2 = new JLabel();
        text = "  " + game.player2.name + " (" + game.player2.elo + ")";
        p2.setText(text);
        p2.setFont(new Font("Arial", Font.PLAIN, size));
        p2.setForeground(Color.WHITE);

        outerBoardNorthPanel.add(p2, BorderLayout.CENTER);
        outerBoardNorthPanel.add(settingsMenu, BorderLayout.WEST);

        JPanel outerBoardSouthPanel = new JPanel();
        outerBoardSouthPanel.setLayout(new BorderLayout());

        JLabel p1 = new JLabel();
        text = "  " + game.player1.name + " (" + game.player1.elo + ")";
        p1.setText(text);
        p1.setFont(new Font("Arial", Font.PLAIN, size));
        p1.setForeground(Color.WHITE);

        outerBoardSouthPanel.add(p1, BorderLayout.CENTER);
        JLabel placeholder = new JLabel("Bacon");
        placeholder.setForeground(background);
        outerBoardSouthPanel.add(placeholder, BorderLayout.WEST);

        outerBoardPanel.setLayout(new BorderLayout(5, 5));
        outerBoardPanel.add(outerBoardNorthPanel, BorderLayout.NORTH);
        outerBoardPanel.add(evaluationBar, BorderLayout.WEST);
        outerBoardPanel.add(boardPanel, BorderLayout.CENTER);
        outerBoardPanel.add(outerBoardSouthPanel, BorderLayout.SOUTH);

        // Round Wheel Complementary Colors
        // Dark Blueish #3F6172
        // Light Brown #936751
        // Army #726A3F
        // Reddish Brown #723F48
        p1.setBackground(background);
        p2.setBackground(background);
        outerBoardNorthPanel.setBackground(background);
        outerBoardSouthPanel.setBackground(background);
        placeholder.setBackground(background);
        player1capTimer.setBackground(background);
        player2capTimer.setBackground(background);
        player1timer.setBackground(background);
        player2timer.setBackground(background);
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
        player2ActionButton.setForeground(Color.WHITE);
        player2ActionButton.setFont(new Font("Arial", Font.BOLD, 30));
        upperBufferPanel.add(player2ActionButton);
        player2capTimer.setLayout(new BorderLayout());
        player2capTimer.add(player2captures, BorderLayout.CENTER);
        player2capTimer.add(player2timer, BorderLayout.EAST);
        upperControlPanel.add(upperBufferPanel);
        upperControlPanel.add(player2capTimer);
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
        fireAtt.descriptionEffect = "Kill the target, give space Inferno effect for 1 turn.";
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
        fireUti.cost = 3;
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
        waterUti.cost = 3;
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
        earthUti.descriptionEffect = "2 by 2 area make pieces skip turn (Range + 1).";
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
        airUti.cost = 2;
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
        player1ActionButton.setForeground(Color.WHITE);
        player1ActionButton.setFont(new Font("Arial", Font.BOLD, 30));
        downBufferPanel.add(player1ActionButton);
        player1capTimer.setLayout(new BorderLayout());
        player1capTimer.add(player1captures, BorderLayout.CENTER);
        player1capTimer.add(player1timer, BorderLayout.EAST);
        downControlPanel.add(player1capTimer);
        downControlPanel.add(downBufferPanel);
        controlPanel.add(downControlPanel, createGBC(1, 1, 0, 3));

        updateText(true);

        fullPanel.add(outerBoardPanel);
        fullPanel.add(controlPanel);

        JPanel songPanel = new JPanel();
        songPanel.setBackground(background);
        songPanel.setLayout(new BorderLayout());
        songTitle.setText("Song Name: " + Main.player.getTrackName());
        fullPanel.setBackground(background);
        songTitle.setBackground(background);
        songTitle.setForeground(Color.WHITE);
        songTitle.setFont(new Font("Arial", Font.PLAIN, 15));

        add(fullPanel, BorderLayout.CENTER);
        songPanel.add(songTitle, BorderLayout.WEST);
        add(songPanel, BorderLayout.SOUTH);
    }

    private JButton getSettingsMenu() {
        JButton settingsMenu = new JButton();
        Image settingsIcon = new ImageIcon("SettingsIcon.png").getImage();
        settingsMenu.setIcon(new ImageIcon(settingsIcon.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        settingsMenu.setBackground(background);
        settingsMenu.setOpaque(false);
        settingsMenu.setBorder(null);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem countAsDrawItem = new JMenuItem("Count as Draw");
        JMenuItem stopMusicItem = new JMenuItem("Stop Music");
        JMenuItem nextTrackItem = new JMenuItem("Play next Track");
        JMenuItem returnStartItem = new JMenuItem("Return to Start Screen");

        countAsDrawItem.addActionListener(e -> {
            System.out.println("Both Players accept a draw.");
            game.getWinner(true);
        });

        stopMusicItem.addActionListener(e -> {
            System.out.println("Music Stopped.");
            game.player.stopMusic();
            updateText(false);
        });

        nextTrackItem.addActionListener(e -> {
            System.out.println("Playing next track.");
            game.player.stopMusic();
            game.player.playRandomTrack();
            updateText(false);
        });

        returnStartItem.addActionListener(e -> {
            System.out.println("\nReturning back to Start Screen.");
            WAVPlayer.isPlaying = false;
            Main.player.skipNextTrack = true;
            game.player.stopMusic();
            player1timer.stopTimer(false);
            player2timer.stopTimer(false);
            dispose();
            Main.showStartMenu();
        });

        popupMenu.add(countAsDrawItem);
        popupMenu.add(stopMusicItem);
        popupMenu.add(nextTrackItem);
        popupMenu.add(returnStartItem);

        settingsMenu.addActionListener(e -> popupMenu.show(settingsMenu, 0, settingsMenu.getHeight()));
        return settingsMenu;
    }

    public void updateText(boolean isFirstTurn) {
        Color moveCol = new Color(71, 167, 213);
        Color attCol = new Color(210, 130, 44);
        Color notCol = new Color(82, 66, 66);
        if (game.turn == TurnState.P2MOVEMENT) {
            player2ActionButton.setText("<html>End Movement Phase<html>");
            player2ActionButton.setBackground(moveCol);
            player1ActionButton.setText("Movement Phase");
            player1ActionButton.setBackground(notCol);
        } else if (game.turn == TurnState.P2ATTACK) {
            player2ActionButton.setText("End Attack Phase");
            player2ActionButton.setBackground(attCol);
            player1ActionButton.setText("Attack Phase");
            player1ActionButton.setBackground(notCol);
        } else if (game.turn == TurnState.P1MOVEMENT) {
            player2ActionButton.setText("Movement Phase");
            player2ActionButton.setBackground(notCol);
            player1ActionButton.setText("<html>End Movement Phase<html>");
            player1ActionButton.setBackground(moveCol);
        } else if (game.turn == TurnState.P1ATTACK) {
            player2ActionButton.setText("Attack Phase");
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
        TitledBorder border = BorderFactory.createTitledBorder("Spells:");
        border.setTitleColor(Color.WHITE);
        border.setTitleFont(new Font("Arial", Font.PLAIN, 20));
        spellPanel.setBorder(border);
        player1captures.updateCaptures();
        player2captures.updateCaptures();
        player2tokens.setText(game.player2.spellTokens + " (+" + game.tokenChange + ")  -  " + game.player2.spellsLeft + "/" + game.player2.spellCounter);
        if (game.isP2Attack()) {
            player2moves.setForeground((game.player2.hasAttacked) ? notCol : attCol);
            player2moves.setText("Attack: " + ((game.player2.hasAttacked) ? "0" : "1") + "/1");
        } else {
            player2moves.setForeground((game.player2.movementCounter == 0) ? notCol : moveCol);
            player2moves.setText("Moves: " + game.player2.movementCounter + "/3");
        }
        roundWheel.setRound(game.round);
        roundWheel.updateText();
        player1tokens.setText(game.player1.spellTokens + " (+" + game.tokenChange + ")  -  " + game.player1.spellsLeft + "/" + game.player2.spellCounter);
        if (game.isP1Attack()) {
            player1moves.setForeground((game.player1.hasAttacked) ? notCol : attCol);
            player1moves.setText("Attack: " + ((game.player1.hasAttacked) ? "0" : "1") + "/1");
        } else {
            player1moves.setForeground((game.player1.movementCounter == 0) ? notCol : moveCol);
            player1moves.setText("Moves: " + game.player1.movementCounter + "/3");
        }
        if (game.isP1Move() && game.player1.movementCounter == 0) {
            System.out.println("Player 1 Movement Phase Automatic End");
            ActionListener actionListener = player1ActionButton.getActionListeners()[0]; // Get the action listener
            actionListener.actionPerformed(null); // Trigger the action
        }

        if (game.isP1Attack() && game.player1.hasAttacked && (game.player1.spellsLeft == 0 || game.player1.spellTokens < 2) && game.activeSpell == null) {
            System.out.println("Player 1 Attack Phase Automatic End");
            ActionListener actionListener = player1ActionButton.getActionListeners()[0]; // Get the action listener
            actionListener.actionPerformed(null); // Trigger the action
        }

        if (game.isP2Move() && game.player2.movementCounter == 0) {
            System.out.println("Player 2 Movement Phase Automatic End");
            ActionListener actionListener = player2ActionButton.getActionListeners()[0]; // Get the action listener
            actionListener.actionPerformed(null); // Trigger the action
        }

        if (game.isP2Attack() && game.player2.hasAttacked && (game.player2.spellsLeft == 0 || game.player2.spellTokens < 2) && game.activeSpell == null) {
            System.out.println("Player 2 Attack Phase Automatic End");
            ActionListener actionListener = player2ActionButton.getActionListeners()[0]; // Get the action listener
            actionListener.actionPerformed(null); // Trigger the action
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

    public void updateSong() {
        System.out.println("UPDATING SONG IN GUI");
        songTitle.setText("Song Name: " + Main.player.getTrackName());
        repaint();
    }
}
