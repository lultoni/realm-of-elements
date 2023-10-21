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
        game.window.updateText(false);
    }

    public boolean freeSpellPath(Cell fromCell, Cell targetCell) {
        System.out.println("fromID:" + fromCell.id);
        System.out.println("targetID:" + targetCell.id);
        int dif = targetCell.id - fromCell.id;
        System.out.println("dif:" + dif);
        if (dif == -9 || dif == -8 || dif == -7 || dif == -1 || dif == 1 || dif == 7 || dif == 8 || dif == 9) return true;
        if (game.isOnLineHorizontal(fromCell.id, targetCell.id)) {
            System.out.println("On Horizontal");
            if (fromCell.id > targetCell.id) {
                for (int i = -1; fromCell.id + i > targetCell.id; i--) {
                    if (pathHelper(fromCell, i)) {
                        System.out.println("There is a piece on the horizontal");
                        return false;
                    }
                }
            } else {
                for (int i = 1; fromCell.id + i < targetCell.id; i++) {
                    if (pathHelper(fromCell, i)) {
                        System.out.println("There is a piece on the horizontal");
                        return false;
                    }
                }
            }
        } else if (game.isOnLineVertical(fromCell.id, targetCell.id)) {
            System.out.println("On Vertical");
            if (fromCell.id > targetCell.id) {
                for (int i = -8; fromCell.id + i > targetCell.id; i -= 8) {
                    if (pathHelper(fromCell, i)) {
                        System.out.println("There is a piece on the vertical");
                        return false;
                    }
                }
            } else {
                for (int i = 8; fromCell.id + i < targetCell.id; i += 8) {
                    if (pathHelper(fromCell, i)) {
                        System.out.println("There is a piece on the vertical");
                        return false;
                    }
                }
            }
        } else {
            switch (dif) {
                case -17, -15 -> {
                    System.out.println("case: -17, -15");
                    if (pathHelper(fromCell, -8)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case 17, 15 -> {
                    System.out.println("case: 17, 15");
                    if (pathHelper(fromCell, 8)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case -25, -23 -> {
                    System.out.println("case: -25, -23");
                    if (pathHelper(fromCell, -8) || pathHelper(fromCell, -16)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case 25, 23 -> {
                    System.out.println("case: 25, 23");
                    if (pathHelper(fromCell, 8) || pathHelper(fromCell, 16)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case -6, 10 -> {
                    System.out.println("case: -6, 10");
                    if (pathHelper(fromCell, 1)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case -5, 11 -> {
                    System.out.println("case: -5, 11");
                    if (pathHelper(fromCell, 2) || pathHelper(fromCell, 1)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case 6, -10 -> {
                    System.out.println("case: 6, -10");
                    if (pathHelper(fromCell, -1)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case 5, -11 -> {
                    System.out.println("case: 5, -11");
                    if (pathHelper(fromCell, -2) || pathHelper(fromCell, 1)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case -14 -> {
                    System.out.println("case: -14");
                    if (pathHelper(fromCell, -7)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case -18 -> {
                    System.out.println("case: -18");
                    if (pathHelper(fromCell, -9)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case 14 -> {
                    System.out.println("case: 14");
                    if (pathHelper(fromCell, 7)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case 18 -> {
                    System.out.println("case: 18");
                    if (pathHelper(fromCell, 9)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case -27, -26, -19 -> {
                    System.out.println("case: -27, -26, -19");
                    if (pathHelper(fromCell, -9) || pathHelper(fromCell, -18)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case 27, 26, 19 -> {
                    System.out.println("case: 27, 26, 19");
                    if (pathHelper(fromCell, 9) || pathHelper(fromCell, 18)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case -22, -21, -13 -> {
                    System.out.println("case: -22, -21, -13");
                    if (pathHelper(fromCell, -7) || pathHelper(fromCell, -14)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
                case 22, 21, 13 -> {
                    System.out.println("case: 22, 21, 13");
                    if (pathHelper(fromCell, 7) || pathHelper(fromCell, 14)) {
                        System.out.println("There is a piece between.");
                        return false;
                    }
                }
            }
        }
        System.out.println("Free Path.");
        return true;
    }

    private boolean pathHelper(Cell fromCell, int i) {
        System.out.println("i:" + i);
        boolean isThereAPiece = game.board[fromCell.id + i].currentPiece != null;
        if (isThereAPiece) {
            System.out.println("There is a piece at " + (fromCell.id + i));
            boolean differentCol = game.board[fromCell.id + i].currentPiece.isBlue != fromCell.currentPiece.isBlue;
            System.out.println("differentCol:" + differentCol);
            boolean isGuard = game.board[fromCell.id + i].currentPiece.type == PieceType.GUARD;
            System.out.println("isGuard:" + isGuard);
            return differentCol && isGuard;
        }
        System.out.println("No piece at " + (fromCell.id + i));
        return false;
    }

}
