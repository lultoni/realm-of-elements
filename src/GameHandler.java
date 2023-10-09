public class GameHandler {

    Cell[] board;
    int round;
    TurnState turn;
    Player player1;
    Player player2;
    int tokenChange;

    public GameHandler() {
        init();
    }

    private void init() {
        board = new Cell[64];
        for (int i = 0; i < 64; i++) {
            switch (i) {
                case 0, 7, 8, 15, 18, 21, 27, 28, 35, 36, 42, 45, 48, 55, 56, 63 -> board[i] = new Cell(Terrain.FORREST, CellStatus.OPEN, i);
                case 1, 2, 3, 4, 5, 6, 19, 20, 43, 44, 57, 58, 59, 60, 61, 62 -> board[i] = new Cell(Terrain.PLAINS, CellStatus.OPEN, i);
                case 9, 10, 11, 12, 13, 14, 17, 22, 41, 46, 49, 50, 51, 52, 53, 54 -> board[i] = new Cell(Terrain.LAKE, CellStatus.OPEN, i);
                case 16, 23, 24, 25, 26, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 47 -> board[i] = new Cell(Terrain.MOUNTAIN, CellStatus.OPEN, i);
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
            board[piece.cellID].status = CellStatus.OCCUPIED;
        }
        for (Piece piece: player2.pieces) {
            board[piece.cellID].status = CellStatus.OCCUPIED;
        }
    }

    public void updateTurn() {
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
    }

}
