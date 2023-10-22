import java.util.*;

public class GameHandler {

    int spellFromID;
    int spellCell;
    int spellCell2;
    boolean needsSpellCell;
    boolean needsSpellCell2;
    boolean spellCellCanBeEmpty;
    Spell activeSpell;
    Cell[] board;
    int round;
    TurnState turn;
    Player player1;
    Player player2;
    int tokenChange;
    Piece selectedPiece;
    int oldSelID;
    int fromID;
    GameWindow window;
    SpellEffectHandler spellEffectHandler;

    boolean gameOver;

    public GameHandler(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        init();
    }

    private void init() {
        board = new Cell[64];
        spellEffectHandler = new SpellEffectHandler(this);
        for (int i = 0; i < 64; i++) {
            switch (i) {
                case 0, 7, 8, 15, 18, 21, 27, 28, 35, 36, 42, 45, 48, 55, 56, 63 -> board[i] = new Cell(Terrain.FORREST, CellStatus.OPEN, i, this);
                case 1, 2, 3, 4, 5, 6, 19, 20, 43, 44, 57, 58, 59, 60, 61, 62 -> board[i] = new Cell(Terrain.PLAINS, CellStatus.OPEN, i, this);
                case 9, 10, 11, 12, 13, 14, 17, 22, 41, 46, 49, 50, 51, 52, 53, 54 -> board[i] = new Cell(Terrain.LAKE, CellStatus.OPEN, i, this);
                case 16, 23, 24, 25, 26, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 47 -> board[i] = new Cell(Terrain.MOUNTAIN, CellStatus.OPEN, i, this);
            }
            board[i].spellEffects = spellEffectHandler;
        }
        gameOver = false;
        spellFromID = -1;
        spellCell = -1;
        spellCell2 = -1;
        oldSelID = -1;
        needsSpellCell = false;
        needsSpellCell2 = false;
        spellCellCanBeEmpty = false;
        activeSpell = null;
        round = 1;
        tokenChange = 1;
        turn = TurnState.P1MOVEMENT;
        player1.pieces = new Piece[]{new Guard(49, true), new Guard(50, true), new Guard(51, true), new Guard(52, true), new Guard(53, true), new Mage(57, PieceType.FIRE_MAGE, true), new Mage(58, PieceType.WATER_MAGE, true), new Mage(59, PieceType.SPIRIT_MAGE, true), new Mage(60, PieceType.EARTH_MAGE, true), new Mage(61, PieceType.AIR_MAGE, true)};
        player2.pieces = new Piece[]{new Guard(10, false), new Guard(11, false), new Guard(12, false), new Guard(13, false), new Guard(14, false), new Mage(2, PieceType.AIR_MAGE, false), new Mage(3, PieceType.EARTH_MAGE, false), new Mage(4, PieceType.SPIRIT_MAGE, false), new Mage(5, PieceType.WATER_MAGE, false), new Mage(6, PieceType.FIRE_MAGE, false)};
        updateBoardStates(false);
        this.selectedPiece = null;
        this.fromID = -1;
    }

    public void start() {
        for (Cell cell: board) {
            cell.updateIcon();
        }

        BackgroundMusicPlayer player = new BackgroundMusicPlayer();
        player.addTrack("Arabia (The Medieval Era).wav", 3, 26);
        player.addTrack("Aztec (The Medieval Era).wav", 2, 44);
        player.addTrack("Camelot.wav", 4, 19);
        player.addTrack("Charge Of The Knights.wav", 5, 5);
        player.addTrack("Crusader Kings 2 Main Title (From the Crusader Kings 2 Original Game Soundtrack).wav", 2, 47);
        player.addTrack("Crusaders (From the Crusader Kings 2 Original Game Soundtrack).wav", 4, 10);
        player.addTrack("Dusking Sky Pt. 1.wav", 5, 28);
        player.addTrack("Dusking Sky Pt. 2.wav", 5, 40);
        player.addTrack("Egypt (The Medieval Era).wav", 3, 10);
        player.addTrack("England (The Medieval Era).wav", 4, 9);
        player.addTrack("France (The Medieval Era).wav", 3, 13);
        player.addTrack("Greece (The Medieval Era).wav", 3, 13);
        player.addTrack("In Taberna Revisited.wav", 3, 5);
        player.addTrack("Journey To Absolution (From the Crusader Kings 2 Original Game Soundtrack).wav", 3, 17);
        player.addTrack("Knights Of Jerusalem.wav", 5, 21);
        player.addTrack("Kongo (The Medieval Era).wav", 3, 43);
        player.addTrack("Krak Des Chevaliers (From the Crusader Kings 2 Original Game Soundtrack).wav", 5, 31);
        player.addTrack("Liement me deport.wav", 2, 13);
        player.addTrack("March To Holyland (From the Crusader Kings 2 Original Game Soundtrack).wav", 3, 15);
        player.addTrack("Northwind.wav", 9, 39);
        player.addTrack("Path To Glory (From the Crusader Kings 2 Original Game Soundtrack).wav", 3, 2);
        player.addTrack("Pilgrimage (From the Crusader Kings 2 Original Game Soundtrack).wav", 1, 45);
        player.addTrack("Prophecy.wav", 5, 34);
        player.addTrack("Reverse Dance.wav", 3, 38);
        player.addTrack("Rome (The Medieval Era).wav", 3, 38);
        player.addTrack("Russia (The Medieval Era).wav", 3, 57);
        player.addTrack("Seaside Tavern.wav", 3, 53);
        player.addTrack("Spain (The Medieval Era).wav", 3, 48);
        player.addTrack("The Banquet.wav", 3, 20);
        player.addTrack("The Dynasty.wav", 5, 12);
        player.addTrack("The First Crusade (From the Crusader Kings 2 Original Game Soundtrack).wav", 4, 58);
        player.addTrack("Veni Vidi Vici.wav", 4, 12);
        player.addTrack("Winds of Ithaca.wav", 6, 6);

        player.playRandomTrack();
    }

    public void updateBoardStates(boolean countTimer) {
        for (Cell cell: board) {
            switch (cell.status) {
                case DEATH, BLOCKED -> {
                    if (cell.timer == 0.0F) {
                        cell.status = CellStatus.OPEN;
                        cell.updateIcon();
                    } else {
                        if (countTimer) cell.timer -= 0.5F;
                        if (cell.timer == 0.0F) cell.updateIcon();
                    }
                }
                case OCCUPIED -> cell.status = CellStatus.OPEN;
            }
        }
        for (Piece piece: player1.pieces) {
            if (piece.cellID == -1) continue;
            board[piece.cellID].status = CellStatus.OCCUPIED;
            board[piece.cellID].currentPiece = piece;
            if (countTimer) piece.timer -= 0.5F;
            if (piece.timer == 0.0F) {
                piece.isReflectingSpell = false;
                piece.isSpellProtected = false;
                piece.isAttackProtected = false;
                piece.isSkippingTurn = false;
            }
            board[piece.cellID].updateIcon();
            piece.hasMoved = false;
            board[piece.cellID].updateIcon();
        }
        for (Piece piece: player2.pieces) {
            if (piece.cellID == -1) continue;
            board[piece.cellID].status = CellStatus.OCCUPIED;
            board[piece.cellID].currentPiece = piece;
            if (countTimer) piece.timer -= 0.5F;
            if (piece.timer == 0.0F) {
                piece.isReflectingSpell = false;
                piece.isSpellProtected = false;
                piece.isAttackProtected = false;
                piece.isSkippingTurn = false;
            }
            board[piece.cellID].updateIcon();
            piece.hasMoved = false;
            board[piece.cellID].updateIcon();
        }
        spellFromID = -1;
        spellCell = -1;
        spellCell2 = -1;
        needsSpellCell = false;
        needsSpellCell2 = false;
        spellCellCanBeEmpty = false;
        activeSpell = null;
    }

    public void updateTurn() {
        selectedPiece = null;
        fromID = -1;
        switch (turn) {
            case P1MOVEMENT -> {
                WAVPlayer.play("MovementPhaseOver.wav");
                turn = TurnState.P1ATTACK;
                updateBoardStates(true);
            }
            case P1ATTACK -> {
                WAVPlayer.play("AttackPhaseOver.wav");
                turn = TurnState.P2MOVEMENT;
                updateBoardStates(false);
            }
            case P2MOVEMENT -> {
                WAVPlayer.play("MovementPhaseOver.wav");
                turn = TurnState.P2ATTACK;
                updateBoardStates(true);
            }
            case P2ATTACK -> {
                WAVPlayer.play("AttackPhaseOver.wav");
                turn = TurnState.P1MOVEMENT;
                round++;
                nextRound();
            }
        }
        System.out.println("Evaluation: " + evaluate(board, player1, player2));
    }

    private void nextRound() {
        if (round % 5 == 0) {
            tokenChange++;
            WAVPlayer.play("SpellTokenGainIncrease.wav");
            if (round % 10 == 0) {
                player1.spellCounter += 1;
                player2.spellCounter += 1;
                WAVPlayer.play("SpellMaxIncrease.wav");
            }
        } else {
            WAVPlayer.play("NextRound.wav");
        }
        player1.spellTokens += tokenChange;
        player2.spellTokens += tokenChange;
        player1.movementCounter = 3;
        player2.movementCounter = 3;
        player1.hasAttacked = false;
        player2.hasAttacked = false;
        player1.spellsLeft = player1.spellCounter;
        player2.spellsLeft = player2.spellCounter;
        updateBoardStates(false);
    }

    public int fetchGuardID(int cellID, int attackerID) {
        int bestCell = -1;
        int dif = attackerID - cellID;

        for (int cellDif : getDifferences(dif)) {
            try {
                int potentialCell = cellID + cellDif;
                if (isValidCell(cellID, potentialCell)) {
                    int pieceScore = scorer(potentialCell);
                    if (pieceScore > 0 && (bestCell == -1 || pieceScore > scorer(bestCell))) {
                        bestCell = potentialCell;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.println("!!! Guard fetch ID Error (array index out of bounds)");
            }
        }

        return bestCell;
    }

    private boolean isValidCell(int sourceCell, int targetCell) {
        return board[targetCell].currentPiece != null &&
                board[targetCell].currentPiece.type == PieceType.GUARD &&
                board[targetCell].currentPiece.isBlue == board[sourceCell].currentPiece.isBlue;
    }

    private int[] getDifferences(int dif) {
        return switch (dif) {
            case -9 -> new int[] { -8, -1, -7, 7, 1, 8, 9 };
            case -8 -> new int[] { -9, -7, -1, 1, 7, 9, 8 };
            case -7 -> new int[] { -8, 1, -9, 9, -1, 8, 7 };
            case -1 -> new int[] { -9, 7, -8, 8, 9, -7, 1 };
            case 1 -> new int[] { 8, 1, 7, -7, -1, -8, -9 };
            case 9 -> new int[] { 9, 7, 1, -1, -7, -9, -8 };
            case 8 -> new int[] { 8, -1, 9, -9, 1, -8, -7 };
            case 7 -> new int[] { 9, -7, 8, -8, -9, 7, -1 };
            default -> new int[0];
        };
    }

    private int scorer(int id) {
        int score = 0;
        switch (id) {
            case 27, 28, 35, 36 -> score = 10;
            case 19, 20, 26, 29, 34, 37, 43, 44 -> score = 9;
            case 18, 21, 42, 45 -> score = 8;
            case 10, 11, 12, 13, 17, 25, 33, 41, 22, 30, 38, 46, 50, 51, 52, 53 -> score = 7;
            case 9, 14, 49, 54 -> score = 6;
            case 0, 7, 56, 63 -> score = 4;
            case 1, 2, 3, 4, 5, 6, 8, 15, 16, 23, 24, 31, 32, 39, 40, 47, 48, 55, 57, 58, 59, 60, 61, 62 -> score = 5;
        }
        return score;
    }

    public boolean matchUpMages(Piece defender, Piece attacker) {
        // Does the attacker have a type advantage?
        boolean back = false;
        switch (attacker.type) {
            case AIR_MAGE -> back = defender.type.equals(PieceType.EARTH_MAGE);
            case FIRE_MAGE -> back = defender.type.equals(PieceType.AIR_MAGE);
            case EARTH_MAGE -> back = defender.type.equals(PieceType.WATER_MAGE);
            case WATER_MAGE -> back = defender.type.equals(PieceType.FIRE_MAGE);
        }
        return back;
    }

    public boolean hasTargetInRange(Piece piece) {
        int range = getRange(board[piece.cellID]);
        Cell[] cellsInRange = getCellsInRange(piece.cellID, range);
        for (Cell cell: cellsInRange) {
            if (cell.currentPiece != null && cell.currentPiece.isBlue != piece.isBlue) {
                return true;
            }
        }
        return false;
    }

    public Cell[] getCellsInRange(int cellID, int range) {
        ArrayList<Cell> preformBack = new ArrayList<>();
        int i;
        if (range >= 1) {
            i = -9;
            if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
            i = -8;
            if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
            i = -7;
            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
            i = -1;
            if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
            i = 1;
            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
            i = 7;
            if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
            i = 8;
            if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
            i = 9;
            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
            if (range >= 2) {
                i = -18;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                i = -17;
                if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                i = -16;
                if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
                i = -15;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = -14;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = -10;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                i = -6;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = -2;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                i = 2;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = 6;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                i = 10;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = 14;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                i = 15;
                if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                i = 16;
                if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
                i = 17;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = 18;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                if (range >= 3) {
                    i = -27;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -26;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -25;
                    if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -24;
                    if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
                    i = -23;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -22;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -21;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0 && (cellID + 1) % 8 != 0 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -19;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -13;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0 && (cellID + 1) % 8 != 0 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -11;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -5;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0 && (cellID + 1) % 8 != 0 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -3;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 3;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0 && (cellID + 1) % 8 != 0 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 5;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0 && cellID % 8 != 0 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 11;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 13;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0 && cellID % 8 != 0 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 19;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 21;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0 && cellID % 8 != 0 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 22;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 23;
                    if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 24;
                    if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
                    i = 25;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 26;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 27;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    if (range >= 4) {
                        i = -36;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -28;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -20;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -12;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -4;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 4;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 12;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 20;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 28;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -35;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 29;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -34;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 30;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -33;
                        if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 31;
                        if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -32;
                        if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
                        i = 32;
                        if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
                        i = -31;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 33;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -30;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 34;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -29;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 35;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -28;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -20;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -12;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -4;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 4;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 12;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 20;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 28;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 36;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        if (range >= 5) {
                            i = -45;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -37;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -29;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -21;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -13;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -5;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 3;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 11;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 19;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 27;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 35;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -44;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 36;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -43;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 37;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -42;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 38;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -41;
                            if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 39;
                            if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -40;
                            if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
                            i = 40;
                            if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
                            i = -39;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 41;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -38;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 42;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -37;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 43;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -36;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 44;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -35;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -27;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -19;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -11;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -3;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 5;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 13;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 21;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 29;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 37;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 45;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0 && (cellID + 4) % 8 != 0 && (cellID + 3) % 8 != 0 && (cellID + 2) % 8 != 0 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        }
                    }
                }
            }
        }
        boolean ascending = true;
        switch (turn) {
            case P1ATTACK, P1MOVEMENT -> ascending = true;
            case P2ATTACK, P2MOVEMENT -> ascending = false;
        }
        System.out.println("The Cell-list is " + preformBack.size() + " items long.");
        Cell[] back = arrayTrimmer(preformBack.toArray(new Cell[0]));
        System.out.println("After trimming: " + back.length);
        back = quickSort(back, 0, back.length - 1, ascending);
        return back;
    }

    private Cell[] quickSort(Cell[] arr, int low, int high, boolean ascending) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high, ascending);
            quickSort(arr, low, pivotIndex - 1, ascending);
            quickSort(arr, pivotIndex + 1, high, ascending);
        }
        return arr;
    }

    private int partition(Cell[] arr, int low, int high, boolean ascending) {
        int pivot = arr[high].id;
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if ((ascending && arr[j].id < pivot) || (!ascending && arr[j].id > pivot)) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private void swap(Cell[] arr, int i, int j) {
        Cell temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static Cell[] arrayTrimmer(Cell[] inputArray) {
        if (inputArray == null || inputArray.length == 0) {
            return inputArray;
        }
        Set<Cell> uniqueCells = new HashSet<>();
        ArrayList<Cell> uniqueCellList = new ArrayList<>();
        for (Cell cell : inputArray) {
            if (uniqueCells.add(cell)) {
                uniqueCellList.add(cell);
            }
        }
        Cell[] uniqueArray = new Cell[uniqueCellList.size()];
        uniqueCellList.toArray(uniqueArray);
        return uniqueArray;
    }

    public boolean isMageOnGoodTerrain(Piece piece) {
        switch (piece.type) {
            case WATER_MAGE -> {
                return board[piece.cellID].type == Terrain.LAKE;
            }
            case EARTH_MAGE -> {
                return board[piece.cellID].type == Terrain.FORREST;
            }
            case FIRE_MAGE -> {
                return board[piece.cellID].type == Terrain.PLAINS;
            }
            case AIR_MAGE -> {
                return board[piece.cellID].type == Terrain.MOUNTAIN;
            }
            case GUARD, SPIRIT_MAGE -> {
                return false;
            }
        }
        return false;
    }

    public boolean isMageOnBadTerrain(Piece piece) {
        switch (piece.type) {
            case WATER_MAGE -> {
                return board[piece.cellID].type == Terrain.MOUNTAIN;
            }
            case EARTH_MAGE -> {
                return board[piece.cellID].type == Terrain.PLAINS;
            }
            case FIRE_MAGE -> {
                return board[piece.cellID].type == Terrain.LAKE;
            }
            case AIR_MAGE -> {
                return board[piece.cellID].type == Terrain.FORREST;
            }
            case GUARD, SPIRIT_MAGE -> {
                return false;
            }
        }
        return false;
    }

    public int getRange(Cell cell) {
        int range = 0;
        Piece piece = cell.currentPiece;
        if (piece == null || piece.type == PieceType.GUARD) return range;
        if (isMageOnGoodTerrain(piece)) {
            range = 3;
        } else if (isMageOnBadTerrain(piece)) {
            range = 1;
        } else {
            range = 2;
        }
        return range;
    }

    public Player getCurrentPlayer() {
        Player player = null;
        switch (turn) {
            case P1ATTACK, P1MOVEMENT -> player = player1;
            case P2ATTACK, P2MOVEMENT -> player = player2;
        }
        return player;
    }

    public Player getNotCurrentPlayer() {
        Player player = null;
        switch (turn) {
            case P1ATTACK, P1MOVEMENT -> player = player2;
            case P2ATTACK, P2MOVEMENT -> player = player1;
        }
        return player;
    }

    public int getWinner(boolean isDraw) {
        if (isDraw) {
            gameOver = true;
            updatePlayerDatabase();
            WAVPlayer.play("GameOver.wav");
            return 0;
        }
        return getWinner();
    }

    public int getWinner() {
        int eval = evaluate();
        if (eval == 10000 || eval == -10000) {
            gameOver = true;
            updatePlayerDatabase();
            WAVPlayer.play("GameOver.wav");
            return eval;
        }
        return 0;
    }

    private void updatePlayerDatabase() { // TODO hope this works
        // Calculate the new Elo and GamesPlayed values for player 1 and player 2.
        player1.calculateElo(player2, false, true);
        player2.calculateElo(player1, false, true);
        int newEloPlayer1 = player1.elo;
        int newEloPlayer2 = player2.elo;
        int newGamesPlayedPlayer1 = player1.gamesPlayed + 1; // Implement this function to calculate the new GamesPlayed for player 1.
        int newGamesPlayedPlayer2 = player1.gamesPlayed + 1; // Implement this function to calculate the new GamesPlayed for player 2.

        // Update player 1's record in the database.
        String updatePlayer1Command = "UPDATE Players SET Elo = " + newEloPlayer1 + ", GamesPlayed = " + newGamesPlayedPlayer1 + " WHERE Name = 'Player 1';";
        DBH.SQL_command(updatePlayer1Command);

        // Update player 2's record in the database.
        String updatePlayer2Command = "UPDATE Players SET Elo = " + newEloPlayer2 + ", GamesPlayed = " + newGamesPlayedPlayer2 + " WHERE Name = 'Player 2';";
        DBH.SQL_command(updatePlayer2Command);
    }



    public boolean isOnLineHorizontal(int spellCell, int spellCell2) {
        int row1 = spellCell / 8;
        int row2 = spellCell2 / 8;
        return row1 == row2;
    }

    public boolean isOnLineVertical(int spellCell, int spellCell2) {
        return spellCell % 8 == spellCell2 % 8;
    }

    public int evaluate() {
        return evaluate(board, player1, player2);
    }

    public static double customScore(int tokens, double score) {
        return Math.sqrt(tokens) * score * 2.3;
    }

    public int evaluate(Cell[] board, Player playerBlue, Player playerRed) {
        int score = 0;
        final int okScore = 10;
        final int spellScore = 30;
        final int goodScore = 50;
        final int bestScore = 100;
        final int winScore = 10000;

        boolean rSM = false;
        boolean bSM = false;

        double scoreBlue = customScore(playerBlue.spellTokens, spellScore);
        double scoreRed = customScore(playerRed.spellTokens, spellScore);

        score += scoreBlue;
        score -= scoreRed;

        for (Cell cell: board) {
            if (cell.currentPiece != null) {
                switch(cell.currentPiece.type) {
                    case AIR_MAGE, EARTH_MAGE, FIRE_MAGE, WATER_MAGE -> {
                        if (isMageOnGoodTerrain(cell.currentPiece)) {
                            score += (cell.currentPiece.isBlue) ? bestScore : -bestScore;
                        } else if (isMageOnBadTerrain(cell.currentPiece)) {
                            score += (cell.currentPiece.isBlue) ? okScore : -okScore;
                        } else {
                            score += (cell.currentPiece.isBlue) ? goodScore : -goodScore;
                        }
                    }
                    case GUARD, SPIRIT_MAGE -> {
                        score += (cell.currentPiece.isBlue) ? goodScore : -goodScore;
                        if (cell.currentPiece.type == PieceType.SPIRIT_MAGE) {
                            if (cell.currentPiece.isBlue) {
                                bSM = true;
                            } else {
                                rSM = true;
                            }
                        }
                    }
                }
            }
        }

        if (!rSM) return winScore;
        if (!bSM) return -winScore;

        return score;
    }

    public boolean isDifferentColor(int cell1, int cell2) {
        return board[cell1].currentPiece.isBlue != board[cell2].currentPiece.isBlue;
    }

    public boolean canP1Move() {
        return (isP1Move() && player1.movementCounter > 0);
    }

    public boolean isP1Move() {
        return turn == TurnState.P1MOVEMENT;
    }

    public boolean canP2Move() {
        return (isP2Move() && player2.movementCounter > 0);
    }

    public boolean isP2Move() {
        return turn == TurnState.P2MOVEMENT;
    }

    public boolean canP1Attack() {
        return isP1Attack() && !player1.hasAttacked;
    }

    public boolean canP2Attack() {
        return isP2Attack() && !player2.hasAttacked;
    }

    public boolean isP1Turn() {
        return turn == TurnState.P1MOVEMENT || turn == TurnState.P1ATTACK;
    }

    public boolean isP1Attack() {
        return turn == TurnState.P1ATTACK;
    }

    public boolean isP2Attack() {
        return turn == TurnState.P2ATTACK;
    }

    public void selectPiece(Cell cell, boolean isMoving) {
        System.out.println("selectPiece() - isMoving:" + isMoving);
        if (isMoving) {
            System.out.println("Select Piece - Move");
            if (!cell.currentPiece.hasMoved && !cell.currentPiece.isSkippingTurn) {
                selectHelper(cell);
                WAVPlayer.play("SelectingPiece.wav");
            }
        } else {
            System.out.println("Select Piece - Attack");
            selectHelper(cell);
            WAVPlayer.play("SelectingPiece.wav");
        }
    }

    private void selectHelper(Cell cell) {
        System.out.println("SelectHelper");
        selectedPiece = cell.currentPiece;
        oldSelID = cell.id;
        fromID = cell.id;
        System.out.println("Done");
    }

    public void resetSelection() {
        selectedPiece = null;
        fromID = -1;
    }

    public void setCellPieceNull(int cellID) {
        board[cellID].currentPiece = null;
        board[cellID].updateIcon();
        board[cellID].status = CellStatus.OPEN;

    }

    public boolean canAttackSelect(Cell cell) {
        boolean isNoSelectedPiece = selectedPiece == null;
        boolean isCellOccupied = cell.currentPiece != null;
        boolean isCellPieceBlue = false;
        boolean isCellPieceNotSkippingTurn = false;
        if (isCellOccupied) {
            isCellPieceBlue = cell.currentPiece.isBlue;
            isCellPieceNotSkippingTurn = !cell.currentPiece.isSkippingTurn;
        }
        return isNoSelectedPiece && isCellOccupied &&
                ((isP1Attack() && isCellPieceBlue) || (isP2Attack() && !isCellPieceBlue) && isCellPieceNotSkippingTurn);
    }

    public void setSpellCell(int id) {
        spellCell = id;
        needsSpellCell = false;
        System.out.println("Set SC1 " + id);
        WAVPlayer.play("SelectingPiece.wav");
    }

    public void setSpellCell2(int id) {
        spellCell2 = id;
        needsSpellCell2 = false;
        System.out.println("Set SC2 " + id);
        WAVPlayer.play("SelectingPiece.wav");
    }

    public String[] generateMoves() {
        String[] moves = {"m1f_0", "m1t_0", "m2f_0", "m2t_0", "m3f_0", "m3t_0", "af_0", "at_0", "s1f_0", "s1t_0"};
        return moves;
    }

}
