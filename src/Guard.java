public class Guard extends Piece {
    public Guard(int cellID, PieceType type, boolean isBlue) {
        super(cellID, type, isBlue);
    }
    public Guard(int cellID, boolean isBlue) {
        super(cellID, PieceType.GUARD, isBlue);
    }
}
