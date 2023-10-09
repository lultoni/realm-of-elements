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
    }

    public void start() {

    }

    public void updateTurn() {
        System.out.println("updateTurn bef - " + turn);
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
        System.out.println("updateTurn af - " + turn);
    }

    private void nextRound() {
        if (round % 5 == 0) {
            tokenChange++;
        }
        player1.spellTokens += tokenChange;
        player2.spellTokens += tokenChange;
    }

}
