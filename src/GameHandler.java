public class GameHandler {

    Cell[] board;
    int round;
    TurnState turn;
    Player player1;
    Player player2;
    int tokenChange;
    Piece selectedPiece;
    int fromID;

    public GameHandler() {
        init();
    }

    private void init() {
        board = new Cell[64];
        for (int i = 0; i < 64; i++) {
            switch (i) {
                case 0, 7, 8, 15, 18, 21, 27, 28, 35, 36, 42, 45, 48, 55, 56, 63 -> board[i] = new Cell(Terrain.FORREST, CellStatus.OPEN, i, this);
                case 1, 2, 3, 4, 5, 6, 19, 20, 43, 44, 57, 58, 59, 60, 61, 62 -> board[i] = new Cell(Terrain.PLAINS, CellStatus.OPEN, i, this);
                case 9, 10, 11, 12, 13, 14, 17, 22, 41, 46, 49, 50, 51, 52, 53, 54 -> board[i] = new Cell(Terrain.LAKE, CellStatus.OPEN, i, this);
                case 16, 23, 24, 25, 26, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 47 -> board[i] = new Cell(Terrain.MOUNTAIN, CellStatus.OPEN, i, this);
            }
        }
        round = 1;
        tokenChange = 1;
        turn = TurnState.P1MOVEMENT;
        this.player1 = new HumanPlayer();
        this.player2 = new ComputerPlayer();
        player1.pieces = new Piece[]{new Guard(49, true), new Guard(50, true), new Guard(51, true), new Guard(52, true), new Guard(53, true), new Mage(57, PieceType.FIRE_MAGE, true), new Mage(58, PieceType.WATER_MAGE, true), new Mage(59, PieceType.SPIRIT_MAGE, true), new Mage(60, PieceType.EARTH_MAGE, true), new Mage(61, PieceType.AIR_MAGE, true)};
        player2.pieces = new Piece[]{new Guard(10, false), new Guard(11, false), new Guard(12, false), new Guard(13, false), new Guard(14, false), new Mage(2, PieceType.AIR_MAGE, false), new Mage(3, PieceType.EARTH_MAGE, false), new Mage(4, PieceType.SPIRIT_MAGE, false), new Mage(5, PieceType.WATER_MAGE, false), new Mage(6, PieceType.FIRE_MAGE, false)};
        updateBoardStates();
        this.selectedPiece = null;
        this.fromID = -1;
    }

    public void start() {

    }

    public void updateBoardStates() {
        for (Cell cell: board) {
            switch (cell.status) {
                case DEATH, BLOCKED -> {
                    if (cell.timer == 0) {
                        cell.status = CellStatus.OPEN;
                    } else {
                        cell.timer--;
                    }
                }
                case OCCUPIED -> cell.status = CellStatus.OPEN;
            }
        }
        for (Piece piece: player1.pieces) {
            if (piece.cellID == -1) continue;
            board[piece.cellID].status = CellStatus.OCCUPIED;
            board[piece.cellID].currentPiece = piece;
            board[piece.cellID].updateIcon();
            piece.hasMoved = false;
        }
        for (Piece piece: player2.pieces) {
            if (piece.cellID == -1) continue;
            board[piece.cellID].status = CellStatus.OCCUPIED;
            board[piece.cellID].currentPiece = piece;
            board[piece.cellID].updateIcon();
            piece.hasMoved = false;
        }
    }

    public void updateTurn() {
        selectedPiece = null;
        fromID = -1;
        switch (turn) {
            case P1MOVEMENT -> turn = TurnState.P1ATTACK;
            case P1ATTACK -> turn = TurnState.P2MOVEMENT;
            case P2MOVEMENT -> turn = TurnState.P2ATTACK;
            case P2ATTACK -> {
                turn = TurnState.P1MOVEMENT;
                round++;
                nextRound();
            }
        }
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
        updateBoardStates();
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
}
