public class SpellEffectHandler {

    GameHandler game;

    public SpellEffectHandler(GameHandler game) {
        this.game = game;
    }

    public void o_f(Cell targetCell) {
        removePiece(targetCell);
        infernoEffect(1, targetCell);
        updates(targetCell);
    }

    public void o_w(Cell targetCell) {
        removePiece(targetCell);
        updates(targetCell);
    }

    public void o_e(Cell targetCell) {
        removePiece(targetCell);
        earthAttackEffect(targetCell);
        updates(targetCell);
    }

    public void o_a(Cell targetCell) {
        removePiece(targetCell);
        boolean pushUp = true;
        switch (game.turn) {
            case P2ATTACK, P2MOVEMENT -> pushUp = false;
        }
        pushBackPieces(targetCell, pushUp);
        updates(targetCell);
    }

    public void o_s(Cell targetCell) {
        removePiece(targetCell);
        game.getCurrentPlayer().spellTokens += 1;
        updates(targetCell);
    }

    private void infernoEffect(float timer, Cell targetCell) {
        targetCell.status = CellStatus.DEATH;
        targetCell.timer = timer;
    }

    private void earthAttackEffect(Cell targetCell) {
        targetCell.status = CellStatus.BLOCKED;
        targetCell.timer = (float) 2;
    }

    private void pushBackPieces(Cell targetCell, boolean pushUp) {
        for (Cell cell: game.getCellsInRange(targetCell.id, 1)) {
            if (cell.currentPiece != null) {
                pushBack(cell.currentPiece, pushUp);
            }
        }
    }

    private void pushBack(Piece currentPiece, boolean pushUp) {
        if (pushUp && currentPiece.cellID >= 8) {
            if (game.board[currentPiece.cellID - 8].currentPiece == null) {
                game.board[currentPiece.cellID].currentPiece = null;
                currentPiece.cellID -= 8;
                game.board[currentPiece.cellID - 8].currentPiece = currentPiece;
            }
        } else if (!pushUp && currentPiece.cellID <= 55) {
            if (game.board[currentPiece.cellID + 8].currentPiece == null) {
                game.board[currentPiece.cellID].currentPiece = null;
                currentPiece.cellID += 8;
                game.board[currentPiece.cellID + 8].currentPiece = currentPiece;
            }
        }
    }

    private void removePiece(Cell targetCell) {
        targetCell.currentPiece.cellID = -1;
        targetCell.currentPiece = null;
    }

    private void updates(Cell targetCell) {
        targetCell.updateIcon();
        game.window.updateText(false, false);
    }

}
