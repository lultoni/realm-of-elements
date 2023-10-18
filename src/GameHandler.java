import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    public GameHandler() {
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
        this.player1 = new HumanPlayer();
        this.player2 = new ComputerPlayer();
        player1.pieces = new Piece[]{new Guard(49, true), new Guard(50, true), new Guard(51, true), new Guard(52, true), new Guard(53, true), new Mage(57, PieceType.FIRE_MAGE, true), new Mage(58, PieceType.WATER_MAGE, true), new Mage(59, PieceType.SPIRIT_MAGE, true), new Mage(60, PieceType.EARTH_MAGE, true), new Mage(61, PieceType.AIR_MAGE, true)};
        player2.pieces = new Piece[]{new Guard(10, false), new Guard(11, false), new Guard(12, false), new Guard(13, false), new Guard(14, false), new Mage(2, PieceType.AIR_MAGE, false), new Mage(3, PieceType.EARTH_MAGE, false), new Mage(4, PieceType.SPIRIT_MAGE, false), new Mage(5, PieceType.WATER_MAGE, false), new Mage(6, PieceType.FIRE_MAGE, false)};
        updateBoardStates(false);
        this.selectedPiece = null;
        this.fromID = -1;
    }

    public void start() {

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
                turn = TurnState.P1ATTACK;
                updateBoardStates(true);
            }
            case P1ATTACK -> {
                turn = TurnState.P2MOVEMENT;
                updateBoardStates(false);
            }
            case P2MOVEMENT -> {
                turn = TurnState.P2ATTACK;
                updateBoardStates(true);
            }
            case P2ATTACK -> {
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
        }
        player1.spellTokens += tokenChange;
        player2.spellTokens += tokenChange;
        player1.movementCounter = 3;
        player2.movementCounter = 3;
        player1.hasAttacked = false;
        player2.hasAttacked = false;
        player1.spellCounter += (round % 10 == 0) ? 1 : 0;
        player2.spellCounter += (round % 10 == 0) ? 1 : 0;
        player1.spellsLeft = player1.spellCounter;
        player2.spellsLeft = player2.spellCounter;
        updateBoardStates(false);
    }

    public int fetchGuardID(int cellID, int attackerID) {
        int bestCell = -1;
        int dif = attackerID - cellID;
        switch (dif) {
            case -9 -> {
                int cellDif1 = -8;
                int cellDif2 = -1;
                int c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                int c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                if (c1 != 0 && (c1 >= c2)) {
                    bestCell = cellID + cellDif1;
                } else if (c2 != 0) {
                    bestCell = cellID + cellDif2;
                } else {
                    cellDif1 = -7;
                    cellDif2 = 7;
                    c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                    c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                    if (c1 != 0 && (c1 >= c2)) {
                        bestCell = cellID + cellDif1;
                    } else if (c2 != 0) {
                        bestCell = cellID + cellDif2;
                    } else {
                        cellDif1 = 1;
                        cellDif2 = 8;
                        c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                        c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                        if (c1 != 0 && (c1 >= c2)) {
                            bestCell = cellID + cellDif1;
                        } else if (c2 != 0) {
                            bestCell = cellID + cellDif2;
                        } else {
                            cellDif1 = 9;
                            if (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) bestCell = cellID + cellDif1;
                        }
                    }
                }
            }
            case -8 -> {
                int cellDif1 = -9;
                int cellDif2 = -7;
                int c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                int c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                if (c1 != 0 && (c1 >= c2)) {
                    bestCell = cellID + cellDif1;
                } else if (c2 != 0) {
                    bestCell = cellID + cellDif2;
                } else {
                    cellDif1 = -1;
                    cellDif2 = 1;
                    c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                    c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                    if (c1 != 0 && (c1 >= c2)) {
                        bestCell = cellID + cellDif1;
                    } else if (c2 != 0) {
                        bestCell = cellID + cellDif2;
                    } else {
                        cellDif1 = 7;
                        cellDif2 = 9;
                        c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                        c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                        if (c1 != 0 && (c1 >= c2)) {
                            bestCell = cellID + cellDif1;
                        } else if (c2 != 0) {
                            bestCell = cellID + cellDif2;
                        } else {
                            cellDif1 = 8;
                            if (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) bestCell = cellID + cellDif1;
                        }
                    }
                }
            }
            case -7 -> {
                int cellDif1 = -8;
                int cellDif2 = 1;
                int c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                int c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                if (c1 != 0 && (c1 >= c2)) {
                    bestCell = cellID + cellDif1;
                } else if (c2 != 0) {
                    bestCell = cellID + cellDif2;
                } else {
                    cellDif1 = -9;
                    cellDif2 = 9;
                    c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                    c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                    if (c1 != 0 && (c1 >= c2)) {
                        bestCell = cellID + cellDif1;
                    } else if (c2 != 0) {
                        bestCell = cellID + cellDif2;
                    } else {
                        cellDif1 = -1;
                        cellDif2 = 8;
                        c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                        c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                        if (c1 != 0 && (c1 >= c2)) {
                            bestCell = cellID + cellDif1;
                        } else if (c2 != 0) {
                            bestCell = cellID + cellDif2;
                        } else {
                            cellDif1 = 7;
                            if (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) bestCell = cellID + cellDif1;
                        }
                    }
                }
            }
            case -1 -> {
                int cellDif1 = -9;
                int cellDif2 = 7;
                int c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                int c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                if (c1 != 0 && (c1 >= c2)) {
                    bestCell = cellID + cellDif1;
                } else if (c2 != 0) {
                    bestCell = cellID + cellDif2;
                } else {
                    cellDif1 = -8;
                    cellDif2 = 8;
                    c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                    c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                    if (c1 != 0 && (c1 >= c2)) {
                        bestCell = cellID + cellDif1;
                    } else if (c2 != 0) {
                        bestCell = cellID + cellDif2;
                    } else {
                        cellDif1 = 9;
                        cellDif2 = -7;
                        c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                        c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                        if (c1 != 0 && (c1 >= c2)) {
                            bestCell = cellID + cellDif1;
                        } else if (c2 != 0) {
                            bestCell = cellID + cellDif2;
                        } else {
                            cellDif1 = 1;
                            if (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) bestCell = cellID + cellDif1;
                        }
                    }
                }
            }
            case 1 -> {
                int cellDif1 = 9;
                int cellDif2 = -7;
                int c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                int c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                if (c1 != 0 && (c1 >= c2)) {
                    bestCell = cellID + cellDif1;
                } else if (c2 != 0) {
                    bestCell = cellID + cellDif2;
                } else {
                    cellDif1 = -8;
                    cellDif2 = 8;
                    c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                    c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                    if (c1 != 0 && (c1 >= c2)) {
                        bestCell = cellID + cellDif1;
                    } else if (c2 != 0) {
                        bestCell = cellID + cellDif2;
                    } else {
                        cellDif1 = -9;
                        cellDif2 = 7;
                        c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                        c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                        if (c1 != 0 && (c1 >= c2)) {
                            bestCell = cellID + cellDif1;
                        } else if (c2 != 0) {
                            bestCell = cellID + cellDif2;
                        } else {
                            cellDif1 = -1;
                            if (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) bestCell = cellID + cellDif1;
                        }
                    }
                }
            }
            case 9 -> {
                int cellDif1 = 8;
                int cellDif2 = 1;
                int c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                int c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                if (c1 != 0 && (c1 >= c2)) {
                    bestCell = cellID + cellDif1;
                } else if (c2 != 0) {
                    bestCell = cellID + cellDif2;
                } else {
                    cellDif1 = -7;
                    cellDif2 = 7;
                    c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                    c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                    if (c1 != 0 && (c1 >= c2)) {
                        bestCell = cellID + cellDif1;
                    } else if (c2 != 0) {
                        bestCell = cellID + cellDif2;
                    } else {
                        cellDif1 = -1;
                        cellDif2 = -8;
                        c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                        c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                        if (c1 != 0 && (c1 >= c2)) {
                            bestCell = cellID + cellDif1;
                        } else if (c2 != 0) {
                            bestCell = cellID + cellDif2;
                        } else {
                            cellDif1 = -9;
                            if (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) bestCell = cellID + cellDif1;
                        }
                    }
                }
            }
            case 8 -> {
                int cellDif1 = 9;
                int cellDif2 = 7;
                int c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                int c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                if (c1 != 0 && (c1 >= c2)) {
                    bestCell = cellID + cellDif1;
                } else if (c2 != 0) {
                    bestCell = cellID + cellDif2;
                } else {
                    cellDif1 = -1;
                    cellDif2 = 1;
                    c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                    c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                    if (c1 != 0 && (c1 >= c2)) {
                        bestCell = cellID + cellDif1;
                    } else if (c2 != 0) {
                        bestCell = cellID + cellDif2;
                    } else {
                        cellDif1 = -7;
                        cellDif2 = -9;
                        c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                        c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                        if (c1 != 0 && (c1 >= c2)) {
                            bestCell = cellID + cellDif1;
                        } else if (c2 != 0) {
                            bestCell = cellID + cellDif2;
                        } else {
                            cellDif1 = -8;
                            if (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) bestCell = cellID + cellDif1;
                        }
                    }
                }
            }
            case 7 -> {
                int cellDif1 = 8;
                int cellDif2 = -1;
                int c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                int c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                if (c1 != 0 && (c1 >= c2)) {
                    bestCell = cellID + cellDif1;
                } else if (c2 != 0) {
                    bestCell = cellID + cellDif2;
                } else {
                    cellDif1 = -9;
                    cellDif2 = 9;
                    c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                    c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                    if (c1 != 0 && (c1 >= c2)) {
                        bestCell = cellID + cellDif1;
                    } else if (c2 != 0) {
                        bestCell = cellID + cellDif2;
                    } else {
                        cellDif1 = 1;
                        cellDif2 = -8;
                        c1 = (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif1) : 0;
                        c2 = (board[cellID + cellDif2].currentPiece != null && board[cellID + cellDif2].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif2].currentPiece.isBlue == board[cellID].currentPiece.isBlue) ? scorer(cellID + cellDif2) : 0;
                        if (c1 != 0 && (c1 >= c2)) {
                            bestCell = cellID + cellDif1;
                        } else if (c2 != 0) {
                            bestCell = cellID + cellDif2;
                        } else {
                            cellDif1 = -7;
                            if (board[cellID + cellDif1].currentPiece != null && board[cellID + cellDif1].currentPiece.type == PieceType.GUARD  && board[cellID + cellDif1].currentPiece.isBlue == board[cellID].currentPiece.isBlue) bestCell = cellID + cellDif1;
                        }
                    }
                }
            }
        }
        return bestCell;
    }

    private int scorer(int id) {
        int score;
        switch (id) {
            case 27, 28, 35, 36 -> score = 10;
            case 19, 20, 26, 29, 34, 37, 43, 44 -> score = 9;
            case 18, 21, 42, 45 -> score = 8;
            case 10, 11, 12, 13, 17, 25, 33, 41, 22, 30, 38, 46, 50, 51, 52, 53 -> score = 7;
            case 9, 14, 49, 54 -> score = 6;
            case 0, 7, 56, 63 -> score = 4;
            default -> score = 5;
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
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = -17;
                if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                i = -16;
                if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
                i = -15;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = -14;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                i = -10;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = -6;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                i = -2;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = 2;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                i = 6;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = 10;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                i = 14;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = 15;
                if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                i = 16;
                if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
                i = 17;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                i = 18;
                if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                if (range >= 3) {
                    i = -27;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -26;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -25;
                    if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -24;
                    if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
                    i = -23;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -22;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -21;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -19;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -13;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -11;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -5;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -3;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 3;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -5;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = -11;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 13;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 19;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 21;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 22;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 23;
                    if (cellID + i >= 0 && cellID + i <= 63 && cellID % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 24;
                    if (cellID + i >= 0 && cellID + i <= 63) preformBack.add(board[cellID + i]);
                    i = 25;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 1) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 26;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                    i = 27;
                    if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0) preformBack.add(board[cellID + i]);
                    if (range >= 4) {
                        i = -36;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -28;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -20;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -12;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -4;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 4;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 12;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 20;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 28;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -35;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 29;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -34;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 30;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
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
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 34;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -29;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 35;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -28;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -20;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -12;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = -4;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 4;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 12;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 20;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 28;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0) preformBack.add(board[cellID + i]);
                        i = 36;
                        if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0) preformBack.add(board[cellID + i]);
                        if (range >= 5) {
                            i = -45;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -37;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -29;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -21;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -13;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -5;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 3;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 11;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 19;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 27;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 35;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -44;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 36;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 3) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -43;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 37;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 2) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -42;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 38;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID - 1) % 8 != 0) preformBack.add(board[cellID + i]);
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
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 42;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 2) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -37;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 43;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 3) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -36;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 44;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 4) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -35;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -27;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -19;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -11;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = -3;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 5;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 13;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 21;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 29;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 37;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0) preformBack.add(board[cellID + i]);
                            i = 45;
                            if (cellID + i >= 0 && cellID + i <= 63 && (cellID + 5) % 8 != 0) preformBack.add(board[cellID + i]);
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

    public int getWinner() {
        int eval = evaluate();
        if (eval == 10000 || eval == -10000) {
            return eval;
        }
        return 0;
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

    public int evaluate(Cell[] board, Player playerBlue, Player playerRed) {
        int score = 0;
        final int okScore = 10;
        final int goodScore = 50;
        final int bestScore = 100;
        final int winScore = 10000;

        boolean rSM = false;
        boolean bSM = false;

        score += playerBlue.spellTokens * goodScore;
        score -= playerRed.spellTokens * goodScore;

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
        return (isP2Move() && player1.movementCounter > 0);
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

    public boolean isP2Turn() {
        return turn == TurnState.P2MOVEMENT || turn == TurnState.P2ATTACK;
    }

    public boolean isP1Attack() {
        return turn == TurnState.P1ATTACK;
    }

    public boolean isP2Attack() {
        return turn == TurnState.P2ATTACK;
    }

    public void selectPiece(Cell cell, boolean isMoving) {
        if (isMoving) {
            System.out.println("Select Piece - Move");
            if (!cell.currentPiece.hasMoved && !cell.currentPiece.isSkippingTurn) selectHelper(cell);
        } else {
            System.out.println("Select Piece - Attack");
            selectHelper(cell);
        }
    }

    private void selectHelper(Cell cell) {
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
    }

    public void setSpellCell2(int id) {
        spellCell2 = id;
        needsSpellCell2 = false;
        System.out.println("Set SC2 " + id);
    }

}
