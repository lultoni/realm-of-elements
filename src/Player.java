public abstract class Player {
    boolean hasAttacked;
    int spellTokens;
    Piece[] pieces;
    int movementCounter;
    public Player() {
        this.spellTokens = 5;
        this.movementCounter = 3;
        this.hasAttacked = false;
    }
}
