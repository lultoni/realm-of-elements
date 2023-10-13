public abstract class Player {
    boolean hasAttacked;
    int spellTokens;
    Piece[] pieces;
    int movementCounter;
    int spellCounter;
    int spellsLeft;
    public Player() {
        this.spellTokens = 5;
        this.movementCounter = 3;
        this.hasAttacked = false;
        this.spellCounter = 2;
        this.spellsLeft = this.spellCounter;
    }
}
