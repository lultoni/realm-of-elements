public abstract class Piece {
    int cellID;
    PieceType type;
    boolean isBlue;
    boolean hasMoved;
    boolean isSpellProtected;
    boolean isReflectingSpell;
    boolean isAttackProtected;
    float timer;

    public Piece(int cellID, PieceType type, boolean isBlue) {
        this.cellID = cellID;
        this.type = type;
        this.isBlue = isBlue;
        this.hasMoved = false;
    }
}
