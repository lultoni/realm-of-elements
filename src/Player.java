public abstract class Player {
    int spellTokens;
    Piece[] pieces;
    int movementCounter;
    public Player() {
        this.spellTokens = 5;
        this.movementCounter = 3;
    }
}
