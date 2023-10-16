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
        pushBackPieces(targetCell, pushUp, 1, 1);
        updates(targetCell);
    }

    public void o_s(Cell targetCell) {
        removePiece(targetCell);
        if (game.getNotCurrentPlayer().spellTokens > 0) {
            game.getCurrentPlayer().spellTokens += 1;
            game.getNotCurrentPlayer().spellTokens -= 1;
        }
        updates(targetCell);
    }

    public void d_f(Cell mageCell, Cell guardCell) {
        attackProtectCell(mageCell);
        updates(mageCell);
        if (guardCell != null) {
            attackProtectCell(guardCell);
            updates(guardCell);
        }
    }

    public void d_w(Cell mageCell, Cell guardCell) {
        spellProtectCell(mageCell);
        updates(mageCell);
        if (guardCell != null) {
            spellProtectCell(guardCell);
            updates(guardCell);
        }
    }

    public void d_e(Cell mageCell, Cell guardCell) {
        spellProtectCell(mageCell);
        updates(mageCell);
        if (guardCell != null) {
            spellProtectCell(guardCell);
            updates(guardCell);
        }
    }

    public void d_a(Cell mageCell, Cell guardCell) {
        reflectSpellCell(mageCell);
        updates(mageCell);
        if (guardCell != null) {
            reflectSpellCell(guardCell);
            updates(guardCell);
        }
    }

    public void d_s(Cell mageCell, Cell guardCell) {
        spellProtectCell(mageCell);
        updates(mageCell);
        if (guardCell != null) {
            spellProtectCell(guardCell);
            updates(guardCell);
        }
    }

    public void u_f(Cell cell1, Cell cell2, Cell cell3) {
        infernoEffect(2, cell1);
        infernoEffect(2, cell2);
        infernoEffect(2, cell3);
        updates(cell1);
        updates(cell2);
        updates(cell3);
    }

    public void u_w() {
        Cell cell = null;
        Piece mage = null;
        for (Piece piece: game.getCurrentPlayer().pieces) {
            if (piece.type == PieceType.WATER_MAGE) {
                cell = game.board[piece.cellID];
                mage = piece;
                break;
            }
        }
        boolean pushUp = true;
        switch (game.turn) {
            case P2ATTACK, P2MOVEMENT -> pushUp = false;
        }
        pushBackPieces(cell, pushUp, game.getRange(game.board[mage.cellID]) + 2, 2);
        updates(cell);
    }

    public void u_e(Cell cell1, Cell cell2, Cell cell3, Cell cell4) {
        skipTurnEffect(1, cell1);
        skipTurnEffect(1, cell2);
        skipTurnEffect(1, cell3);
        skipTurnEffect(1, cell4);
        updates(cell1);
        updates(cell2);
        updates(cell3);
        updates(cell4);
    }

    public void u_a(Cell fromCell, Cell targetCell) {
        targetCell.currentPiece = fromCell.currentPiece;
        targetCell.currentPiece.cellID = targetCell.id;
        fromCell.currentPiece = null;
        fromCell.updateIcon();
        fromCell.status = CellStatus.OPEN;
        targetCell.updateIcon();
        targetCell.status = CellStatus.OCCUPIED;

    }

    public void u_s(Cell cell1, Cell cell2) {
        Piece piece = cell1.currentPiece;
        cell1.currentPiece = cell2.currentPiece;
        cell1.currentPiece.cellID = cell1.id;
        cell2.currentPiece = piece;
        cell2.currentPiece.cellID = cell2.id;
        game.selectedPiece = null;
        updates(cell1);
        updates(cell2);
    }

    private void spellProtectCell(Cell cell) {
        cell.currentPiece.isSpellProtected = true;
        cell.currentPiece.timer = 1;
    }

    private void attackProtectCell(Cell cell) {
        cell.currentPiece.isAttackProtected = true;
        cell.currentPiece.timer = 1;
    }

    private void reflectSpellCell(Cell cell) {
        cell.currentPiece.isReflectingSpell = true;
        cell.currentPiece.timer = 1;
    }

    private void infernoEffect(float timer, Cell targetCell) {
        targetCell.status = CellStatus.DEATH;
        targetCell.timer = timer;
    }

    private void skipTurnEffect(float timer, Cell targetCell) {
        if (targetCell.currentPiece != null) {
            targetCell.currentPiece.isSkippingTurn = true;
            targetCell.currentPiece.timer = timer;
        }
    }

    private void earthAttackEffect(Cell targetCell) {
        targetCell.status = CellStatus.BLOCKED;
        targetCell.timer = (float) 2;
    }

    private void pushBackPieces(Cell targetCell, boolean pushUp, int range, int spaces) {
        for (Cell cell: game.getCellsInRange(targetCell.id, range)) {
            if (cell.currentPiece != null && cell.currentPiece.isBlue != pushUp) {
                pushBack(cell.currentPiece, pushUp, spaces);
            }
        }
    }

    private void pushBack(Piece currentPiece, boolean pushUp, int spaces) {
        int dif = 8 * spaces;
        if (pushUp && currentPiece.cellID >= dif) {
            if (game.board[currentPiece.cellID - dif].currentPiece == null) {
                game.board[currentPiece.cellID].currentPiece = null;
                game.board[currentPiece.cellID].updateIcon();
                currentPiece.cellID -= dif;
                game.board[currentPiece.cellID].currentPiece = currentPiece;
                game.board[currentPiece.cellID].updateIcon();
            }
        } else if (!pushUp && currentPiece.cellID <= 63 - dif) {
            if (game.board[currentPiece.cellID + dif].currentPiece == null) {
                game.board[currentPiece.cellID].currentPiece = null;
                game.board[currentPiece.cellID].updateIcon();
                currentPiece.cellID += dif;
                game.board[currentPiece.cellID].currentPiece = currentPiece;
                game.board[currentPiece.cellID].updateIcon();
            }
        }
        if (spaces == 2) pushBack(currentPiece, pushUp, spaces - 1);
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
