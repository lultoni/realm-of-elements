import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Cell extends JButton {

    Terrain type;
    CellStatus status;
    int id;
    float timer;
    Piece currentPiece;
    GameHandler game;
    SpellEffectHandler spellEffects;

    public Cell(Terrain type, CellStatus status, int id, GameHandler game) {
        this.type = type;
        this.status = status;
        this.id = id;
        this.timer = 0;
        this.currentPiece = null;
        this.game = game;
        initialize();
    }

    private void initialize() {
        setFont(new Font("Arial", Font.BOLD, 20));
        updateIcon();
        addActionListener(e -> {
            if (game.getWinner() != 0) return;
            System.out.println("\nCell-" + id + " clicked");
            printSelf();
            needSpellCell();
            if (game.activeSpell != null && !game.needsSpellCell2) {
                castEffectOfSpell();
            }
            movement();
            attacking();
            updateIcon();
            game.window.updateText(false, false);
        });
    }

    private void movement() {
        System.out.println("Movement");
        if (canMove()) {
            System.out.println("canMove");
            if (status == CellStatus.OCCUPIED && game.selectedPiece == null) {
                System.out.println("selecting");
                game.selectPiece(this, true);
            } else if (isOpenOrDeath() && checkRange(true) && noSideSwitch()) {
                if (status == CellStatus.DEATH) {
                    removePiece();
                    takeAwayMovement();
                } else {
                    movePiece();
                }

            } else {
                game.resetSelection();
            }
        }
    }

    private void removePiece() {
        game.board[game.fromID].currentPiece.cellID = -1;
        game.setCellPieceNull(game.fromID);
        game.resetSelection();
    }

    private void takeAwayMovement() {
        if (game.isP1Move()) {
            game.player1.movementCounter--;
        } else {
            game.player2.movementCounter--;
        }
    }

    private boolean noSideSwitch() {
        boolean isLeftEdge = id % 8 == 0;
        boolean isRightEdge = (id + 1) % 8 == 0;
        boolean isAdjacentToLeft = game.fromID + 9 == id || game.fromID + 1 == id || game.fromID - 7 == id;
        boolean isAdjacentToRight = game.fromID - 9 == id || game.fromID - 1 == id || game.fromID + 7 == id;

        return !(isLeftEdge && isAdjacentToLeft) && !(isRightEdge && isAdjacentToRight);
    }

    private boolean isOpenOrDeath() {
        return status == CellStatus.OPEN || status == CellStatus.DEATH;
    }


    private boolean canMove() {
        System.out.println("start canMove");
        boolean isOccupied = status == CellStatus.OCCUPIED;
        System.out.println("isOccupied:" + isOccupied);
        boolean canP1Move = game.canP1Move();
        System.out.println("canP1Move:" + canP1Move);
        boolean canP2Move = game.canP2Move();
        System.out.println("canP2Move:" + canP2Move);

        if (isOccupied) {
            System.out.println("isOcu");
            if (currentPiece != null) {
                System.out.println("hasPiece");
                boolean hasMoved = !currentPiece.hasMoved;
                boolean isBlue = currentPiece.isBlue;
                System.out.println("!hasMoved:" + hasMoved);
                System.out.println("isBlue:" + isBlue);
                return ((canP1Move && isBlue) || (canP2Move && !isBlue)) && hasMoved;
            }
        } else if (isOpenOrDeath()) {
            if (game.selectedPiece != null) {
                boolean hasMoved = !game.selectedPiece.hasMoved;
                boolean isBlue = game.selectedPiece.isBlue;
                return ((canP1Move && isBlue) || (canP2Move && !isBlue)) && hasMoved;
            }
        }

        return false;
    }

    private void guardProtecting(int guardID) {
        game.board[guardID].currentPiece.cellID = -1;
        game.setCellPieceNull(guardID);
        game.resetSelection();
    }

    private void currentPieceSets() {
        currentPiece = game.selectedPiece;
        currentPiece.hasMoved = true;
        currentPiece.cellID = id;
    }

    private void moveAttackHelper() {
        currentPieceSets();
        game.setCellPieceNull(game.fromID);
        game.resetSelection();
        updateIcon();
        status = CellStatus.OCCUPIED;
    }

    private void normal1v1() {
        currentPiece.cellID = -1;
        moveAttackHelper();
    }

    private void movePiece() {
        System.out.println("Move Piece");
        moveAttackHelper();
        takeAwayMovement();
        System.out.println("Done");
    }

    private void attacking() { // TODO sometimes can attack twice (ui not updating??)
        if (game.canP1Attack() || game.canP2Attack()) {
            if (status == CellStatus.OCCUPIED) {
                if (game.canAttackSelect(this)) {
                    game.selectPiece(this, false);
                } else if (checkRange(false) && game.isDifferentColor(game.fromID, id) && !currentPiece.isAttackProtected) {
                    System.out.println("Attack Piece");

                    if (game.selectedPiece.type == PieceType.GUARD) {
                        if (currentPiece.type != PieceType.GUARD) {
                            System.out.println("Guard attacking Mage");
                            int guardID = game.fetchGuardID(id, game.selectedPiece.cellID);
                            if (guardID != -1) {
                                guardProtecting(guardID);
                            } else {
                                normal1v1();
                            }
                        } else {
                            System.out.println("Guard attacking Guard");
                            normal1v1();
                        }
                    } else if (currentPiece.type == PieceType.GUARD) {
                        System.out.println("Mage attacking Guard");
                        normal1v1();
                        currentPiece = null;
                        status = CellStatus.OPEN;
                    } else {
                        System.out.println("Mage attacking Mage");
                        int guardID = game.fetchGuardID(id, game.selectedPiece.cellID);
                        if (guardID != -1 && !game.matchUpMages(currentPiece, game.board[game.fromID].currentPiece)) {
                            guardProtecting(guardID);
                        } else {
                            normal1v1();
                        }
                    }

                    System.out.println("Done");
                } else {
                    game.resetSelection();
                }
            }
        }
    }

    private void needSpellCell() {
        if (game.activeSpell == null) {
            return;
        }

        System.out.println(game.needsSpellCell2 ? "Need SC2" : "Need SC1");

        try {
            boolean isCurrentPieceNotNull = currentPiece != null;
            boolean isSpellFromIDValid = game.spellFromID >= 0 && game.spellFromID < game.board.length;
            boolean canSetSpellCell = game.spellCellCanBeEmpty || (isCurrentPieceNotNull && game.isDifferentColor(game.spellFromID, id));

            if (isCurrentPieceNotNull && currentPiece.type == PieceType.GUARD && isSpellFromIDValid && game.isDifferentColor(game.spellFromID, id)) {
                if (game.needsSpellCell2) {
                    game.setSpellCell2(id);
                } else {
                    game.setSpellCell(id);
                }
            } else if (canSetSpellCell) {
                if (game.needsSpellCell2) {
                    game.setSpellCell2(id);
                } else {
                    game.setSpellCell(id);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ERROR " + (game.needsSpellCell2 ? "SC2" : "SC1") + " " + e);
        }
    }

    private void giveBackSpellCosts() {
        game.getCurrentPlayer().spellTokens += game.activeSpell.cost;
        game.getCurrentPlayer().spellsLeft++;
        game.window.updateText(false, false);
    }

    private boolean isInSpellRangeSingle(int customRange) {
        boolean isInRange = false;
        for (Cell cell: game.getCellsInRange(game.spellFromID, (customRange == 0) ? game.getRange(game.board[game.spellFromID]) : customRange)) {
            if (cell.id == game.spellCell) {
                isInRange = true;
                break;
            }
        }
        return isInRange;
    }

    private boolean isInSpellRangeDouble() {
        boolean isInRange1 = false;
        boolean isInRange2 = false;
        for (Cell cell: game.getCellsInRange(game.spellFromID, game.getRange(game.board[game.spellFromID]))) {
            if (cell.id == game.spellCell) {
                isInRange1 = true;
                if (isInRange2) break;
            }
            if (cell.id == game.spellCell2) {
                isInRange2 = true;
                if (isInRange1) break;
            }
        }
        return isInRange1 && isInRange2;
    }

    private void castEffectOfSpell() {
        System.out.println("Effect will start now");
        switch (game.activeSpell.type) {
            case OFFENSE -> { // TODO guard protecting
                if (game.spellCell != -1) switch (game.activeSpell.mageElement) {
                    case FIRE_MAGE -> {
                        if (isInSpellRangeSingle(0) && !currentPiece.isSpellProtected && spellEffects.freeSpellPath(game.board[game.spellFromID], this)) {
                            System.out.println("OFFENSE - FIRE_MAGE");
                            if (currentPiece.isReflectingSpell) spellEffects.o_f(game.board[game.spellFromID]);
                            spellEffects.o_f(game.board[game.spellCell]);
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                    case WATER_MAGE -> {
                        if (isInSpellRangeSingle(0) && !currentPiece.isSpellProtected && spellEffects.freeSpellPath(game.board[game.spellFromID], this)) {
                            System.out.println("OFFENSE - WATER_MAGE");
                            if (currentPiece.isReflectingSpell) spellEffects.o_w(game.board[game.spellFromID]);
                            spellEffects.o_w(game.board[game.spellCell]);
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                    case EARTH_MAGE -> { // TODO guard protecting makes it invincible
                        if (isInSpellRangeSingle(0) && !currentPiece.isSpellProtected && spellEffects.freeSpellPath(game.board[game.spellFromID], this)) {
                            System.out.println("OFFENSE - EARTH_MAGE");
                            if (currentPiece.isReflectingSpell) spellEffects.o_e(game.board[game.spellFromID]);
                            spellEffects.o_e(game.board[game.spellCell]);
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                    case AIR_MAGE -> {
                        if (isInSpellRangeSingle(0) && !currentPiece.isSpellProtected && spellEffects.freeSpellPath(game.board[game.spellFromID], this)) {
                            System.out.println("OFFENSE - AIR_MAGE");
                            if (currentPiece.isReflectingSpell) spellEffects.o_a(game.board[game.spellFromID]);
                            spellEffects.o_a(game.board[game.spellCell]);
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                    case SPIRIT_MAGE -> {
                        if (isInSpellRangeSingle(0) && !currentPiece.isSpellProtected && spellEffects.freeSpellPath(game.board[game.spellFromID], this)) {
                            System.out.println("OFFENSE - SPIRIT_MAGE");
                            if (currentPiece.isReflectingSpell) spellEffects.o_s(game.board[game.spellFromID]);
                            spellEffects.o_s(game.board[game.spellCell]);
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                }
            }
            case DEFENSE -> { // TODO not selecting spell
                switch (game.activeSpell.mageElement) {
                    case FIRE_MAGE -> {
                        if (isInSpellRangeSingle(0) && !game.board[game.spellFromID].currentPiece.isSpellProtected) {
                            System.out.println("DEFENSE - FIRE_MAGE");
                            spellEffects.d_f(game.board[game.spellFromID], (game.board[game.spellCell].currentPiece.type == PieceType.GUARD && !game.board[game.spellCell].currentPiece.isSpellProtected) ? game.board[game.spellCell] : null);
                        } else if (!game.board[game.spellFromID].currentPiece.isSpellProtected) {
                            System.out.println("DEFENSE - FIRE_MAGE");
                            spellEffects.d_f(game.board[game.spellFromID], null);
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                    case WATER_MAGE -> {
                        if (isInSpellRangeSingle(0) && !game.board[game.spellFromID].currentPiece.isSpellProtected) {
                            System.out.println("DEFENSE - WATER_MAGE");
                            spellEffects.d_w(game.board[game.spellFromID], (game.board[game.spellCell].currentPiece.type == PieceType.GUARD && !game.board[game.spellCell].currentPiece.isSpellProtected) ? game.board[game.spellCell] : null);
                        } else if (!game.board[game.spellFromID].currentPiece.isSpellProtected) {
                            System.out.println("DEFENSE - WATER_MAGE");
                            spellEffects.d_w(game.board[game.spellFromID], null);
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                    case EARTH_MAGE -> {
                        if (isInSpellRangeSingle(0) && !game.board[game.spellFromID].currentPiece.isSpellProtected) {
                            System.out.println("DEFENSE - EARTH_MAGE");
                            spellEffects.d_e(game.board[game.spellFromID], (game.board[game.spellCell].currentPiece.type == PieceType.GUARD && !game.board[game.spellCell].currentPiece.isSpellProtected) ? game.board[game.spellCell] : null);
                        } else if (!game.board[game.spellFromID].currentPiece.isSpellProtected) {
                            System.out.println("DEFENSE - EARTH_MAGE");
                            spellEffects.d_e(game.board[game.spellFromID], null);
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                    case AIR_MAGE -> {
                        if (isInSpellRangeSingle(0) && !game.board[game.spellFromID].currentPiece.isSpellProtected) {
                            System.out.println("DEFENSE - AIR_MAGE");
                            spellEffects.d_a(game.board[game.spellFromID], (game.board[game.spellCell].currentPiece.type == PieceType.GUARD && !game.board[game.spellCell].currentPiece.isSpellProtected) ? game.board[game.spellCell] : null);
                        } else if (!game.board[game.spellFromID].currentPiece.isSpellProtected) {
                            System.out.println("DEFENSE - AIR_MAGE");
                            spellEffects.d_a(game.board[game.spellFromID], null);
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                    case SPIRIT_MAGE -> {
                        if (isInSpellRangeSingle(0 ) && !game.board[game.spellFromID].currentPiece.isSpellProtected) {
                            System.out.println("DEFENSE - SPIRIT_MAGE");
                            spellEffects.d_s(game.board[game.spellFromID], (game.board[game.spellCell].currentPiece.type == PieceType.GUARD && !game.board[game.spellCell].currentPiece.isSpellProtected) ? game.board[game.spellCell] : null);
                        } else if (!game.board[game.spellFromID].currentPiece.isSpellProtected) {
                            System.out.println("DEFENSE - SPIRIT_MAGE");
                            spellEffects.d_s(game.board[game.spellFromID], null);
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                }
            }
            case UTILITY -> {
                switch (game.activeSpell.mageElement) {
                    case FIRE_MAGE -> {
                        boolean isInRange = isInSpellRangeDouble();
                        if (isInRange && game.isOnLineHorizontal(game.spellCell, game.spellCell2)) {
                            if (game.spellCell >= game.spellCell2) {
                                System.out.println("UTILITY - FIRE_MAGE hor 1>2");
                                if (game.board[game.spellCell].currentPiece == null && game.board[game.spellCell - 1].currentPiece == null && game.board[game.spellCell - 2].currentPiece == null) {
                                    spellEffects.u_f(game.board[game.spellCell], game.board[game.spellCell - 1], game.board[game.spellCell - 2]);
                                } else {
                                    giveBackSpellCosts();
                                }
                            } else {
                                System.out.println("UTILITY - FIRE_MAGE hor 2>1");
                                if (game.board[game.spellCell].currentPiece == null && game.board[game.spellCell + 1].currentPiece == null && game.board[game.spellCell + 2].currentPiece == null) {
                                    spellEffects.u_f(game.board[game.spellCell], game.board[game.spellCell + 1], game.board[game.spellCell + 2]);
                                } else {
                                    giveBackSpellCosts();
                                }
                            }
                        } else if (isInRange && game.isOnLineVertical(game.spellCell, game.spellCell2)) {
                            if (game.spellCell >= game.spellCell2) {
                                System.out.println("UTILITY - FIRE_MAGE ver 1>2");
                                if (game.board[game.spellCell].currentPiece == null && game.board[game.spellCell - 8].currentPiece == null && game.board[game.spellCell - 16].currentPiece == null) {
                                    spellEffects.u_f(game.board[game.spellCell], game.board[game.spellCell - 8], game.board[game.spellCell - 16]);
                                } else {
                                    giveBackSpellCosts();
                                }
                            } else {
                                System.out.println("UTILITY - FIRE_MAGE ver 2>1");
                                if (game.board[game.spellCell].currentPiece == null && game.board[game.spellCell + 8].currentPiece == null && game.board[game.spellCell + 16].currentPiece == null) {
                                    spellEffects.u_f(game.board[game.spellCell], game.board[game.spellCell + 8], game.board[game.spellCell + 16]);
                                } else {
                                    giveBackSpellCosts();
                                }
                            }
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                    case WATER_MAGE -> {
                        if (id == game.spellFromID) {
                            System.out.println("UTILITY - WATER_MAGE");
                            spellEffects.u_w();
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                    case EARTH_MAGE -> {
                        if (isInSpellRangeDouble()) {
                            switch (game.spellCell - game.spellCell2) {
                                case -9, 9 -> {
                                    System.out.println("UTILITY - EARTH_MAGE");
                                    spellEffects.u_e(game.board[game.spellCell], game.board[game.spellCell + 1], game.board[game.spellCell2 - 1], game.board[game.spellCell2]);
                                }
                                case -7, 7 -> {
                                    System.out.println("UTILITY - EARTH_MAGE");
                                    spellEffects.u_e(game.board[game.spellCell - 1], game.board[game.spellCell], game.board[game.spellCell2], game.board[game.spellCell2 + 1]);
                                }
                            }
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                    case AIR_MAGE -> {
                        if (isInSpellRangeSingle(1) && currentPiece == null) {
                            System.out.println("UTILITY - AIR_MAGE");
                            spellEffects.u_a(game.board[game.spellFromID], this);
                        } else {
                            giveBackSpellCosts();
                        }

                    }
                    case SPIRIT_MAGE -> { // TODO && is not the same spellID
                        if (game.board[game.spellCell].currentPiece != null && game.board[game.spellCell2].currentPiece != null) {
                            if (game.board[game.spellCell].currentPiece.isBlue == game.board[game.spellFromID].currentPiece.isBlue && game.board[game.spellCell2].currentPiece.isBlue == game.board[game.spellFromID].currentPiece.isBlue) {
                                System.out.println("UTILITY - SPIRIT_MAGE");
                                spellEffects.u_s(game.board[game.spellCell], game.board[game.spellCell2]);
                            }
                        } else {
                            giveBackSpellCosts();
                        }
                    }
                }
            }
        }
        game.activeSpell = null;
        game.window.updateText(false, false);
    }

    private boolean checkRange(boolean movement) {
        if (game.fromID == -1) return false;
        switch (game.fromID - id) {
            case -9, -8, -7, -1, 1, 7, 8, 9 -> {
                return true;
            }
            case -18, -17, -16, -15, -14, -10, -6, -2, 2, 6, 10, 14, 15, 16, 17, 18 -> {
                int dif = game.fromID - id;
                boolean noLongSwitch = !((id % 8 == 0) && (dif == -10 || dif == -2 || dif == 6 || dif == 14)) && !(((id + 1) % 8 == 0) && (dif == 10 || dif == 2 || dif == -6 || dif == -14));
                if (!noLongSwitch) return false;
                if (dif == -18 && id >= 9 && game.board[id - 9].currentPiece != null ||
                        dif == -17 && id >= 9 && game.board[id - 9].currentPiece != null && game.board[id - 8].currentPiece != null ||
                        dif == -16 && id >= 9 && game.board[id - 9].currentPiece != null && game.board[id - 8].currentPiece != null && game.board[id - 7].currentPiece != null ||
                        dif == -15 && id >= 9 && game.board[id - 7].currentPiece != null && game.board[id - 8].currentPiece != null ||
                        dif == -14 && id >= 7 && game.board[id - 7].currentPiece != null ||
                        dif == -10 && id >= 9 && game.board[id - 9].currentPiece != null && game.board[id - 1].currentPiece != null ||
                        dif == -6 && id >= 7 && id <= 62 && game.board[id - 7].currentPiece != null && game.board[id + 1].currentPiece != null ||
                        dif == -2 && id >= 7 && id <= 56 && game.board[id - 9].currentPiece != null && game.board[id - 1].currentPiece != null && game.board[id + 7].currentPiece != null ||
                        dif == 18 && id <= 54 && game.board[id + 9].currentPiece != null ||
                        dif == 17 && id <= 54 && game.board[id + 9].currentPiece != null && game.board[id + 8].currentPiece != null ||
                        dif == 16 && id <= 54 && game.board[id + 9].currentPiece != null && game.board[id + 8].currentPiece != null && game.board[id + 7].currentPiece != null ||
                        dif == 15 && id <= 55 && game.board[id + 7].currentPiece != null && game.board[id + 8].currentPiece != null ||
                        dif == 14 && id <= 56 && game.board[id + 7].currentPiece != null ||
                        dif == 10 && id <= 54 && game.board[id + 9].currentPiece != null && game.board[id + 1].currentPiece != null ||
                        dif == 6 && id >= 1 && id <= 56 && game.board[id + 7].currentPiece != null && game.board[id - 1].currentPiece != null ||
                        dif == 2 && id >= 7 && id <= 54 && game.board[id + 9].currentPiece != null && game.board[id + 1].currentPiece != null && game.board[id - 7].currentPiece != null) {
                    return false;
                }
                if (movement) return game.isMageOnGoodTerrain(game.board[game.fromID].currentPiece);
            }
        }
        return false;
    }

    public void printSelf() {
        System.out.println("-type:" + type);
        System.out.println("-status:" + status);
        System.out.println("-id:" + id);
        System.out.println("-timer:" + timer);
        System.out.println("-currentPiece:" + currentPiece);
        System.out.println("-game.selectedPiece:" + game.selectedPiece);
        System.out.println("-game.fromID:" + game.fromID);
    }

    public void updateIcon() {
        Image before_ter;
        String location = "";
        Image before_pie;
        Image ter_indicator;
        switch (this.type) {
            case LAKE -> location = "LakeSprite.png";
            case MOUNTAIN -> location = "MountainSprite.png";
            case FORREST -> location = "ForrestSprite.png";
            case PLAINS -> location = "PlainsSprite.png";
        }
        switch (this.status) {
            case DEATH -> location = "DeathEffect.png";
            case BLOCKED -> location = "BlockedEffect.png";
        }
        before_ter = new ImageIcon(location).getImage();
        location = "";
        int height = 70;
        int width = 70;
        if (game.window != null && game.window.getWidth() > 0 && game.window.getHeight() > 0) {
            double dif = 1;
            width = (int) (((game.window.getWidth() / 2) / 8) * dif);
            height = (int) ((game.window.getHeight() / 8) * dif);
        }
        if (this.currentPiece != null) {
            switch (this.currentPiece.type) {
                case GUARD -> location = (currentPiece.isBlue) ? "BlueGuard.png" : "RedGuard.png";
                case AIR_MAGE -> location = (currentPiece.isBlue) ? "BlueAirMage.png" : "RedAirMage.png";
                case FIRE_MAGE -> location = (currentPiece.isBlue) ? "BlueFireMage.png" : "RedFireMage.png";
                case EARTH_MAGE -> location = (currentPiece.isBlue) ? "BlueEarthMage.png" : "RedEarthMage.png";
                case WATER_MAGE -> location = (currentPiece.isBlue) ? "BlueWaterMage.png" : "RedWaterMage.png";
                case SPIRIT_MAGE -> location = (currentPiece.isBlue) ? "BlueSpiritMage.png" : "RedSpiritMage.png";
            }
            if (currentPiece.isAttackProtected) {
                location = "Attack" + location;
            } else if (currentPiece.isReflectingSpell) {
                location = "Reflect" + location;
            } else if (currentPiece.isSpellProtected) {
                location = "Spell" + location;
            } else if (currentPiece.hasMoved || currentPiece.isSkippingTurn) {
                location = "Moved" + location;
            }
            before_pie = new ImageIcon(location).getImage();
            if (game.isMageOnGoodTerrain(currentPiece)) {
                location = "GoodTerrain.png";
            } else if (game.isMageOnBadTerrain(currentPiece)) {
                location = "BadTerrain.png";
            }
            ter_indicator = new ImageIcon(location).getImage();
            int combinedWidth = Math.min(before_ter.getWidth(null), before_pie.getWidth(null));
            int combinedHeight = Math.min(before_ter.getHeight(null), before_pie.getHeight(null));
            BufferedImage combinedImage = new BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = combinedImage.createGraphics();
            g2d.drawImage(before_ter, 0, 0, null);
            g2d.drawImage(before_pie, 0, 0, null);
            g2d.drawImage(ter_indicator, 0, 0, null);
            g2d.dispose();

            Image after = combinedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(after));
        } else {
            setIcon(new ImageIcon(before_ter.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        }
    }

}
