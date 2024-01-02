import java.util.*;

public class GameHandler {

    public BackgroundMusicPlayer player;
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
        // Standard map: "FPPP/FLLL/MLFP/MMMF" 5/5
        // ChatGPT 1: "FMFF/PLMM/FMPP/LLPL" 4/5
        // ChatGPT 2: "LMLM/FMFP/PPPL/MLMF" 2/5
        // ChatGPT 3: "FMPL/LMPP/PMLL/FMFF" 5/5
        // ChatGPT 4: "LPPF/FMPL/MLPL/PLMF" 3/5
        // ChatGPT 5: "PLLF/FMPP/PMML/LFPL" 3/5
        loadMap("FPPP/FLLL/MLFP/MMMF");
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

    private void loadMap(String mapFEN) {
        String[] halves = mapFEN.split("/");
        String fullMap = "";

        for (String lineSplit: halves) {
            fullMap += lineSplit + new StringBuilder(lineSplit).reverse();
        }
        fullMap = fullMap + new StringBuilder(fullMap).reverse();

        for (int i = 0; i < 64; i++) {
            char cellType = fullMap.charAt(i);
            Terrain terrain;
            switch (cellType) {
                case 'P':
                    terrain = Terrain.PLAINS;
                    break;
                case 'F':
                    terrain = Terrain.FORREST;
                    break;
                case 'M':
                    terrain = Terrain.MOUNTAIN;
                    break;
                case 'L':
                    terrain = Terrain.LAKE;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid terrain symbol: " + cellType);
            }

            board[i] = new Cell(terrain, CellStatus.OPEN, i, this);
            board[i].spellEffects = spellEffectHandler;
        }
    }

    public void start() {
        for (Cell cell: board) {
            cell.updateIcon();
        }

        player = Main.player;
        player.playRandomTrack();
        window.updateText(true);
        window.player1timer.startTimer();
//        printBestMovementPhase();
    }

    private void printBestMovementPhase() {
        int bestEval = evaluate();
        String bestMove = "";
        for (String move: movementPhaseMoves()) {
            int[] mNumbers = extractMoves(move);
            int m1f = mNumbers[0];
            int m1t = mNumbers[1];
            int m2f = mNumbers[2];
            int m2t = mNumbers[3];
            int m3f = mNumbers[4];
            int m3t = mNumbers[5];
            doMove(m1f, m1t);
            if (m2f != -1) doMove(m2f, m2t);
            if (m3f != -1) doMove(m3f, m3t);
            int eval = evaluate();
            if ((isP1Turn()) ? eval > bestEval : eval < bestEval) {
                bestEval = eval;
                bestMove = move;
            }
            if (m3f != -1) doMove(m3t, m3f);
            if (m2f != -1) doMove(m2t, m2f);
            doMove(m1t, m1f);
        }
        System.out.println("Best move: " + bestMove);
        System.out.println("Eval of move: " + bestEval);
    }

    private void printBestAttackPhase() {
        int bestEval = evaluate();
        String bestAttack = "";
        for (String attack: attackPhaseAttacks()) {
            int[] aNumbers = extractMoves(attack);
            int af = aNumbers[0];
            int at = aNumbers[1];
            Piece takenPiece = board[at].currentPiece;
            doAttack(af, at);
            int eval = evaluate();
            if ((isP1Turn()) ? eval > bestEval : eval < bestEval) {
                bestEval = eval;
                bestAttack = attack;
            }
            undoAttack(af, at, takenPiece);
        }
        if (bestAttack.isEmpty()) {
            System.out.println("There is no good attack");
        } else {
            System.out.println("Best attack: " + bestAttack);
            System.out.println("Eval of attack: " + bestEval);
        }
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
                        cell.updateIcon();
                    }
                }
                case OCCUPIED -> cell.status = CellStatus.OPEN;
            }
            cell.updateIcon();
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
        if (activeSpell != null) {
            if (activeSpell.type == SpellType.DEFENSE) {
                Piece piece = board[spellFromID].currentPiece;
                SpellEffectHandler spellEffectHandler = new SpellEffectHandler(this);
                switch (piece.type) {
                    case AIR_MAGE -> spellEffectHandler.d_a(board[spellFromID], null);
                    case FIRE_MAGE -> spellEffectHandler.d_f(board[spellFromID], null);
                    case EARTH_MAGE -> spellEffectHandler.d_e(board[spellFromID], null);
                    case WATER_MAGE -> spellEffectHandler.d_w(board[spellFromID], null);
                    case SPIRIT_MAGE -> spellEffectHandler.d_s(board[spellFromID], null);
                }

            } else {
                getCurrentPlayer().spellTokens += activeSpell.cost;
                getCurrentPlayer().spellsLeft++;
                window.updateText(false);
                WAVPlayer.play("SpellCancel.wav");
            }
        }
        switch (turn) {
            case P1MOVEMENT -> {
                WAVPlayer.play("MovementPhaseOver.wav");
                turn = TurnState.P1ATTACK;
                updateBoardStates(true);
//                printBestAttackPhase();
            }
            case P1ATTACK -> {
                WAVPlayer.play("AttackPhaseOver.wav");
                turn = TurnState.P2MOVEMENT;
                updateBoardStates(false);
                window.player1timer.stopTimer(true);
                window.player2timer.startTimer();
//                printBestMovementPhase();
            }
            case P2MOVEMENT -> {
                WAVPlayer.play("MovementPhaseOver.wav");
                turn = TurnState.P2ATTACK;
                updateBoardStates(true);
//                printBestAttackPhase();
            }
            case P2ATTACK -> {
                WAVPlayer.play("AttackPhaseOver.wav");
                turn = TurnState.P1MOVEMENT;
                round++;
                nextRound();
                window.player2timer.stopTimer(true);
                window.player1timer.startTimer();
//                printBestMovementPhase();
            }
        }
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

    public int fetchGuardID(int cellID, int attackerID) { // TODO wrong guard gets taken
        int bestCell = -1;
        int dif = attackerID - cellID;

        for (int cellDif : getDifferences(dif)) {
            if ((cellDif == -9 && cellID % 8 != 0) || (cellDif == 7 && cellID % 8 != 0) || (cellDif == -1 && cellID % 8 != 0) || (cellDif == -7 && (cellID + 1) % 8 != 0) || (cellDif == 9 && (cellID + 1) % 8 != 0) || (cellDif == 1 && (cellID + 1) % 8 != 0) || (cellDif == 8 && cellID < 56) || (cellDif == -8 && cellID > 7)) try {
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
        boolean ascending = isP1Turn();
        Cell[] back = arrayTrimmer(preformBack.toArray(new Cell[0]));
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

    public void getWinner(boolean isDraw) {
        if (isDraw) {
            gameOver = true;
            updatePlayerDatabase(false, true);
            WAVPlayer.play("GameOver.wav");
            window.player1timer.stopTimer(false);
            window.player2timer.stopTimer(false);
            window.updateText(false);
        }
    }

    public int getWinner() {
        int eval = evaluate();
        if (eval == 10000 || eval == -10000) {
            gameOver = true;
            updatePlayerDatabase(eval == 10000, false);
            WAVPlayer.play("GameOver.wav");
            window.player1timer.stopTimer(false);
            window.player2timer.stopTimer(false);
            window.updateText(false);
            return eval;
        }
        return 0;
    }

    public void getWinner(Player player) {
        gameOver = true;
        updatePlayerDatabase(player.equals(player2), false);
        WAVPlayer.play("GameOver.wav");
        window.player1timer.stopTimer(false);
        window.player2timer.stopTimer(false);
        window.updateText(false);
    }

    private void updatePlayerDatabase(boolean isWinPlayer1, boolean isDraw) {
        int p1e = player1.elo;
        int p2e = player2.elo;
        player1.calculateElo(p2e, isWinPlayer1, isDraw);
        player2.calculateElo(p1e, !isWinPlayer1, isDraw);
        int newEloPlayer1 = player1.elo;
        int newEloPlayer2 = player2.elo;
        int newGamesPlayedPlayer1 = player1.gamesPlayed + 1;
        int newGamesPlayedPlayer2 = player2.gamesPlayed + 1;

        System.out.println("\nupdatePlayerDatabase() - pre 1 com (elo:" + newEloPlayer1 + ", gP:" + newGamesPlayedPlayer1 + ")");
        DBH.updatePlayer(newEloPlayer1, newGamesPlayedPlayer1, player1.name);

        System.out.println("updatePlayerDatabase() - pre 2 com (elo:" + newEloPlayer2 + ", gP:" + newGamesPlayedPlayer2 + ")\n");
        DBH.updatePlayer(newEloPlayer2, newGamesPlayedPlayer2, player2.name);
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
        return evaluate(player1, player2);
    }

    public static double spellTokenScore(int tokens, double score) {
        return Math.sqrt(tokens) * score * 2.3;
    }

    public int evaluate(Player playerBlue, Player playerRed) {
        // TODO check important things like:
        int score = 0;
        final int spellScore = 30;
        final int okScore = 10;
        final int goodScore = 50;
        final int bestScore = 100;
        final int winScore = 10000;

        boolean rSM = false;
        boolean bSM = false;

        double scoreBlue = spellTokenScore(playerBlue.spellTokens, spellScore);
        double scoreRed = spellTokenScore(playerRed.spellTokens, spellScore);

        score += (int) scoreBlue;
        score -= (int) scoreRed;

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

    public void timeFunction() {
        long startTime = System.currentTimeMillis();
        movementPhaseMoves();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        long elapsedSeconds = elapsedTime / 1000;
        System.out.println("Milliseconds: " + elapsedTime);
        System.out.println("Seconds: " + elapsedSeconds);
    }

    public ArrayList<String> movementPhaseMoves() {
        ArrayList<String> m1sArr = new ArrayList<>();
        ArrayList<String> m2sArr = new ArrayList<>();
        ArrayList<String> m3sArr = new ArrayList<>();
        String move1;
        String move2 = "-1.-1";
        String move3 = "-1.-1";
        int m1s;
        int m2s;
        int m3s;

        for (Piece p1: getCurrentPlayer().pieces) {
            if (p1.cellID != -1) for (Cell c1: getCellsInRange(p1.cellID, (isMageOnGoodTerrain(p1)) ? 2 : 1)) {
                if (c1.currentPiece == null && canMove(c1, p1)) {
                    int m1f = p1.cellID;
                    int m1t = c1.id;
                    move1 = m1f + "." + m1t;
                    m1sArr.add(move1 + ":" + move2 + ":" + move3);
                    doMove(m1f, m1t);
                    for (Piece p2: getCurrentPlayer().pieces) {
                        if (!p1.equals(p2) && p2.cellID != -1) for (Cell c2: getCellsInRange(p2.cellID, (isMageOnGoodTerrain(p2)) ? 2 : 1)) {
                            if (c2.currentPiece == null && c2.id != m1t && canMove(c2, p2)) {
                                int m2f = p2.cellID;
                                int m2t = c2.id;
                                move2 = m2f + "." + m2t;
                                m2sArr.add(move1 + ":" + move2 + ":" + move3);
                                doMove(m2f, m2t);
                                for (Piece p3: getCurrentPlayer().pieces) {
                                    if (!p1.equals(p3) && !p2.equals(p3) && p3.cellID != -1) for (Cell c3: getCellsInRange(p3.cellID, (isMageOnGoodTerrain(p3)) ? 2 : 1)) {
                                        if (c3.currentPiece == null && c3.id != m1t && c3.id != m2t && canMove(c3, p3)) {
                                            move3 = p3.cellID + "." + c3.id;
                                            m3sArr.add(move1 + ":" + move2 + ":" + move3);
                                        }
                                    }
                                }
                                doMove(m2t, m2f);
                                move3 = "-1.-1";
                            }
                        }
                    }
                    doMove(m1t, m1f);
                    move2 = "-1.-1";
                    move3 = "-1.-1";
                }
            }
            move2 = "-1.-1";
            move3 = "-1.-1";
        }
        m1s = m1sArr.size();
        System.out.println("Possible m1's: " + m1s);
        m2s = m2sArr.size();
        System.out.println("Possible m2's: " + (m2s));
        m3s = m3sArr.size();
        System.out.println("Possible m3's: " + (m3s));

        System.out.println("Possible moves: " + (m3s + m2s + m1s));
        return getCombinedArrayList(m1sArr, m2sArr, m3sArr);
    }

    public ArrayList<String> attackPhaseAttacks() {
        ArrayList<String> attacks = new ArrayList<>();
        for (Piece piece: getCurrentPlayer().pieces) {
            for (Cell cell: getCellsInRange(piece.cellID, 1)) {
                if (cell.currentPiece != null && isDifferentColor(cell.id, piece.cellID)) {
                    attacks.add(piece.cellID + "." + cell.id);
                }
            }
        }
        return attacks;
    }

    private ArrayList<String> getCombinedArrayList(ArrayList<String> ar1, ArrayList<String> ar2, ArrayList<String> ar3) {
        ArrayList<String> combinedList = new ArrayList<>(ar1);
        combinedList.addAll(ar2);
        combinedList.addAll(ar3);
        return combinedList;
    }

    private void doMove(int mf, int mt) {
        board[mt].currentPiece = board[mf].currentPiece;
        board[mt].currentPiece.cellID = mt;
        board[mf].currentPiece = null;
        board[mf].status = CellStatus.OPEN;
        board[mt].status = CellStatus.OCCUPIED;
    }

    private void undoAttack(int af, int at, Piece takenPiece) {
        doMove(at, af);
        board[at].currentPiece = takenPiece;
        board[at].currentPiece.cellID = at;
    }

    private void doAttack(int af, int at) {
        board[at].currentPiece.cellID = -1;
        doMove(af, at);
    }

    private boolean canMove(Cell cell, Piece piece) {
        int id = cell.id;
        int dif = piece.cellID - id;
        boolean noLongSwitch = !((id % 8 == 0) && (dif == -10 || dif == -2 || dif == 6 || dif == 14)) && !(((id + 1) % 8 == 0) && (dif == 10 || dif == 2 || dif == -6 || dif == -14));
        if (!noLongSwitch) return false;
        if (dif == -18 && id >= 9 && board[id - 9].currentPiece != null ||
                dif == -17 && id >= 9 && board[id - 9].currentPiece != null && board[id - 8].currentPiece != null ||
                dif == -16 && id >= 9 && board[id - 9].currentPiece != null && board[id - 8].currentPiece != null && board[id - 7].currentPiece != null ||
                dif == -15 && id >= 9 && board[id - 7].currentPiece != null && board[id - 8].currentPiece != null ||
                dif == -14 && id >= 7 && board[id - 7].currentPiece != null ||
                dif == -10 && id >= 9 && board[id - 9].currentPiece != null && board[id - 1].currentPiece != null ||
                dif == -6 && id >= 7 && id <= 62 && board[id - 7].currentPiece != null && board[id + 1].currentPiece != null ||
                dif == -2 && id >= 7 && id <= 56 && board[id - 9].currentPiece != null && board[id - 1].currentPiece != null && board[id + 7].currentPiece != null ||
                dif == 18 && id <= 54 && board[id + 9].currentPiece != null ||
                dif == 17 && id <= 54 && board[id + 9].currentPiece != null && board[id + 8].currentPiece != null ||
                dif == 16 && id <= 54 && board[id + 9].currentPiece != null && board[id + 8].currentPiece != null && board[id + 7].currentPiece != null ||
                dif == 15 && id <= 55 && board[id + 7].currentPiece != null && board[id + 8].currentPiece != null ||
                dif == 14 && id <= 56 && board[id + 7].currentPiece != null ||
                dif == 10 && id <= 54 && board[id + 9].currentPiece != null && board[id + 1].currentPiece != null ||
                dif == 6 && id >= 1 && id <= 56 && board[id + 7].currentPiece != null && board[id - 1].currentPiece != null ||
                dif == 2 && id >= 7 && id <= 54 && board[id + 9].currentPiece != null && board[id + 1].currentPiece != null && board[id - 7].currentPiece != null) {
            return pieceMovable(piece);
        }
        return pieceMovable(piece);
    }

    private boolean pieceMovable(Piece piece) {
        return !piece.isSkippingTurn && !piece.hasMoved;
    }

    public static int[] extractMoves(String input) {
        String[] moveTokens = input.split(":");
        int[] moves = new int[moveTokens.length * 2];
        int index = 0;
        for (String moveToken : moveTokens) {
            String[] moveParts = moveToken.split("\\.");
            if (moveParts.length == 2) {
                int from = Integer.parseInt(moveParts[0]);
                int to = Integer.parseInt(moveParts[1]);

                moves[index++] = from;
                moves[index++] = to;
            }
        }
        return moves;
    }

    public boolean allPiecesP1Moved() {
        boolean back = true;
        for (Piece piece: player1.pieces) {
            if (!piece.hasMoved && piece.cellID != -1) return false;
        }
        return back;
    }

    public boolean allPiecesP2Moved() {
        boolean back = true;
        for (Piece piece: player2.pieces) {
            if (!piece.hasMoved && piece.cellID != -1) return false;
        }
        return back;
    }
}
