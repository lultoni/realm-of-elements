import javax.swing.*;

public abstract class Piece {
    int cellID;
    PieceType type;
    boolean isBlue;
    boolean hasMoved;

    public Piece(int cellID, PieceType type, boolean isBlue) {
        this.cellID = cellID;
        this.type = type;
        this.isBlue = isBlue;
        this.hasMoved = false;
    }
}
