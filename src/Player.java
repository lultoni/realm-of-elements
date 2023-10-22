public abstract class Player {
    String name;
    int elo;
    int gamesPlayed;
    boolean hasAttacked;
    int spellTokens;
    Piece[] pieces;
    int movementCounter;
    int spellCounter;
    int spellsLeft;
    
    public Player(String name, int elo, int gamesPlayed) {
        this.name = name;
        this.elo = elo;
        this.gamesPlayed = gamesPlayed;
        init();
    }

    private void init() {
        this.spellTokens = 5;
        this.movementCounter = 3;
        this.hasAttacked = false;
        this.spellCounter = 2;
        this.spellsLeft = this.spellCounter;
    }

    public void calculateElo(int opponentElo, boolean isWin, boolean isDraw) {
        System.out.println("\n" + name + ": calculating Elo");
        double winPercentageMyself = (double) 1 / (1 + Math.pow(10, (double) (opponentElo - elo) / 400));
        System.out.println("winPercentageMyself " + winPercentageMyself);
        int k = (gamesPlayed < 30) ? 30 : (elo - opponentElo > 400) ? 32 : 24;
        System.out.println("k " + k);

        elo = (int) (elo + k * (((isDraw) ? 0.5 : (isWin) ? 1 : 0) - winPercentageMyself));
        System.out.println("newElo: " + elo);
    }
}
