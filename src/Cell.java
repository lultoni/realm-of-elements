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
            System.out.println("\nCell-" + id + " clicked");
            printSelf();
            if (game.activeSpell != null && game.spellCell == -1) {
                System.out.println("We need a spell cell dude");
                try {
                    if (currentPiece != null && game.board[game.spellFromID].currentPiece.isBlue != currentPiece.isBlue) {
                        game.spellCell = id;
                        game.needsSpellCell = false;
                    }
                } catch (ArrayIndexOutOfBoundsException use) {
                    System.out.println("SUSSY BAKA ERROR SPELL CELL");
                }
            }
            if (game.activeSpell != null && game.spellCell != -1) {
                System.out.println("Effect will start now");
                switch (game.activeSpell.type) {
                    case OFFENSE -> {
                        switch (game.activeSpell.mageElement) {
                            case FIRE_MAGE -> {
                                boolean isInRange = false;
                                for (Cell cell: game.getCellsInRange(game.spellFromID, game.getRange(game.board[game.spellFromID]))) {
                                    if (cell.id == game.spellCell) {
                                        isInRange = true;
                                        break;
                                    }
                                }
                                if (isInRange) {
                                    System.out.println("OFFENSE - FIRE_MAGE");
                                    spellEffects.o_f(game.board[game.spellCell]);
                                } else {
                                    game.getCurrentPlayer().spellTokens += game.activeSpell.cost;
                                    game.window.updateText(false, false);
                                }
                            }
                            case WATER_MAGE -> {
                                System.out.println("OFFENSE - WATER_MAGE");
                            }
                            case EARTH_MAGE -> {
                                System.out.println("OFFENSE - EARTH_MAGE");
                            }
                            case AIR_MAGE -> {
                                System.out.println("OFFENSE - AIR_MAGE");
                            }
                            case SPIRIT_MAGE -> {
                                System.out.println("OFFENSE - SPIRIT_MAGE");
                            }
                        }
                    }
                    case DEFENSE -> {
                        switch (game.activeSpell.mageElement) {
                            case FIRE_MAGE -> {
                                System.out.println("DEFENSE - FIRE_MAGE");
                            }
                            case WATER_MAGE -> {
                                System.out.println("DEFENSE - WATER_MAGE");
                            }
                            case EARTH_MAGE -> {
                                System.out.println("DEFENSE - EARTH_MAGE");
                            }
                            case AIR_MAGE -> {
                                System.out.println("DEFENSE - AIR_MAGE");
                            }
                            case SPIRIT_MAGE -> {
                                System.out.println("DEFENSE - SPIRIT_MAGE");
                            }
                        }
                    }
                    case UTILITY -> {
                        switch (game.activeSpell.mageElement) {
                            case FIRE_MAGE -> {
                                System.out.println("UTILITY - FIRE_MAGE");
                            }
                            case WATER_MAGE -> {
                                System.out.println("UTILITY - WATER_MAGE");
                            }
                            case EARTH_MAGE -> {
                                System.out.println("UTILITY - EARTH_MAGE");
                            }
                            case AIR_MAGE -> {
                                System.out.println("UTILITY - AIR_MAGE");
                            }
                            case SPIRIT_MAGE -> {
                                System.out.println("UTILITY - SPIRIT_MAGE");
                            }
                        }
                    }
                }
                game.activeSpell = null;
                return;
            }
            boolean canMove = (status == CellStatus.OCCUPIED && !currentPiece.hasMoved && (game.turn == TurnState.P1MOVEMENT && currentPiece.isBlue || game.turn == TurnState.P2MOVEMENT && !currentPiece.isBlue) || status == CellStatus.OPEN && game.selectedPiece != null && !game.selectedPiece.hasMoved && (game.turn == TurnState.P1MOVEMENT && game.selectedPiece.isBlue || game.turn == TurnState.P2MOVEMENT && !game.selectedPiece.isBlue));
//            System.out.println("sel op1 = " + (status == CellStatus.OCCUPIED));
//            System.out.println("sel op2 = " + (game.selectedPiece == null));
//            System.out.println("mov op1 = " + (status == CellStatus.OPEN));
//            System.out.println("mov op2 = " + (game.selectedPiece != null));
//            System.out.println("can = " + canMove);
            if (canMove && ((game.turn == TurnState.P1MOVEMENT && game.player1.movementCounter > 0) || (game.turn == TurnState.P2MOVEMENT && game.player2.movementCounter > 0))) {
                boolean noSwitch = !((id % 8 == 0) && (game.fromID + 9 == id || game.fromID + 1 == id || game.fromID - 7 == id)) && !(((id + 1) % 8 == 0) && (game.fromID - 9 == id || game.fromID - 1 == id || game.fromID + 7 == id));
//                System.out.println("noSwitch:" + noSwitch);
                if (status == CellStatus.OCCUPIED && game.selectedPiece == null) {
                    System.out.println("Select Piece");
                    if (!currentPiece.hasMoved) {
                        game.selectedPiece = currentPiece;
                        game.fromID = id;
                        System.out.println("Done");
                    }
                } else if (status == CellStatus.OPEN && game.selectedPiece != null && checkRange(true) && noSwitch) {
                    System.out.println("Move Piece");
                    currentPiece = game.selectedPiece;
                    currentPiece.hasMoved = true;
                    currentPiece.cellID = id;
                    game.selectedPiece = null;
                    game.board[game.fromID].currentPiece = null;
                    game.board[game.fromID].updateIcon();
                    game.board[game.fromID].status = CellStatus.OPEN;
                    game.fromID = -1;
                    updateIcon();
                    status = CellStatus.OCCUPIED;
                    if (game.turn == TurnState.P1MOVEMENT) {
                        game.player1.movementCounter--;
                    } else {
                        game.player2.movementCounter--;
                    }
                    System.out.println("Done");
                } else {
                    game.selectedPiece = null;
                    game.fromID = -1;
                }
            }
            if (game.turn == TurnState.P1ATTACK && !game.player1.hasAttacked || game.turn == TurnState.P2ATTACK && !game.player2.hasAttacked) {
                 if (status == CellStatus.OCCUPIED) {
                     if (game.selectedPiece == null) {
                         System.out.println("Select Piece");
                         game.selectedPiece = currentPiece;
                         game.fromID = id;
                         System.out.println("Done");
                     } else if (checkRange(false) && game.board[game.fromID].currentPiece.isBlue != currentPiece.isBlue) {
                         System.out.println("Attack Piece");
                         if (game.selectedPiece.type == PieceType.GUARD && currentPiece.type != PieceType.GUARD) {
                             System.out.println("Guard attacking Mage - working");
                             int guardID = game.fetchGuardID(id, game.selectedPiece.cellID);
                             if (guardID != -1) {
                                 game.board[guardID].currentPiece.cellID = -1;
                                 game.board[guardID].currentPiece = null;
                                 game.board[guardID].updateIcon();
                                 game.board[guardID].status = CellStatus.OPEN;
                                 game.selectedPiece = null;
                                 game.fromID = -1;
                             } else {
                                 currentPiece.cellID = -1;
                                 currentPiece = game.selectedPiece;
                                 currentPiece.hasMoved = true;
                                 currentPiece.cellID = id;
                                 game.selectedPiece = null;
                                 game.board[game.fromID].currentPiece = null;
                                 game.board[game.fromID].updateIcon();
                                 game.board[game.fromID].status = CellStatus.OPEN;
                                 game.fromID = -1;
                                 updateIcon();
                                 status = CellStatus.OCCUPIED;
                             }
                             if (game.turn == TurnState.P1ATTACK) {
                                 game.player1.hasAttacked = true;
                             } else {
                                 game.player2.hasAttacked = true;
                             }
                         } else if (game.selectedPiece.type == PieceType.GUARD) {
                             System.out.println("Guard attacking Guard - working");
                             currentPiece.cellID = -1;
                             currentPiece = game.selectedPiece;
                             currentPiece.hasMoved = true;
                             currentPiece.cellID = id;
                             game.selectedPiece = null;
                             game.board[game.fromID].currentPiece = null;
                             game.board[game.fromID].updateIcon();
                             game.board[game.fromID].status = CellStatus.OPEN;
                             game.fromID = -1;
                             updateIcon();
                             status = CellStatus.OCCUPIED;
                             if (game.turn == TurnState.P1ATTACK) {
                                 game.player1.hasAttacked = true;
                             } else {
                                 game.player2.hasAttacked = true;
                             }
                         } else if (currentPiece.type == PieceType.GUARD) {
                             System.out.println("Mage attacking Guard - working");
                             currentPiece.cellID = -1;
                             currentPiece = game.selectedPiece;
                             currentPiece.hasMoved = true;
                             currentPiece.cellID = -1;
                             game.selectedPiece = null;
                             game.board[game.fromID].currentPiece = null;
                             currentPiece = null;
                             game.board[game.fromID].updateIcon();
                             game.board[game.fromID].status = CellStatus.OPEN;
                             status = CellStatus.OPEN;
                             game.fromID = -1;
                             updateIcon();
                             if (game.turn == TurnState.P1ATTACK) {
                                 game.player1.hasAttacked = true;
                             } else {
                                 game.player2.hasAttacked = true;
                             }
                         } else {
                             System.out.println("Mage attacking Mage - working");
                             int guardID = game.fetchGuardID(id, game.selectedPiece.cellID);
                             if (guardID != -1 && !game.matchUpMages(currentPiece, game.board[game.fromID].currentPiece)) {
                                 game.board[guardID].currentPiece.cellID = -1;
                                 game.board[guardID].currentPiece = null;
                                 game.board[guardID].updateIcon();
                                 game.board[guardID].status = CellStatus.OPEN;
                                 game.selectedPiece = null;
                                 game.fromID = -1;
                             } else {
                                 currentPiece.cellID = -1;
                                 currentPiece = game.selectedPiece;
                                 currentPiece.hasMoved = true;
                                 currentPiece.cellID = id;
                                 game.selectedPiece = null;
                                 game.board[game.fromID].currentPiece = null;
                                 game.board[game.fromID].updateIcon();
                                 game.board[game.fromID].status = CellStatus.OPEN;
                                 game.fromID = -1;
                                 updateIcon();
                                 status = CellStatus.OCCUPIED;
                             }
                             if (game.turn == TurnState.P1ATTACK) {
                                 game.player1.hasAttacked = true;
                             } else {
                                 game.player2.hasAttacked = true;
                             }
                         }

                         System.out.println("Done");
                     } else {
                         game.selectedPiece = null;
                         game.fromID = -1;
                     }
                 }
            }
            updateIcon();
            game.window.updateText(false, false);
        });
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
        if (this.currentPiece != null) {
            switch (this.currentPiece.type) {
                case GUARD -> location = (currentPiece.isBlue) ? "BlueGuard.png" : "RedGuard.png";
                case AIR_MAGE -> location = (currentPiece.isBlue) ? "BlueAirMage.png" : "RedAirMage.png";
                case FIRE_MAGE -> location = (currentPiece.isBlue) ? "BlueFireMage.png" : "RedFireMage.png";
                case EARTH_MAGE -> location = (currentPiece.isBlue) ? "BlueEarthMage.png" : "RedEarthMage.png";
                case WATER_MAGE -> location = (currentPiece.isBlue) ? "BlueWaterMage.png" : "RedWaterMage.png";
                case SPIRIT_MAGE -> location = (currentPiece.isBlue) ? "BlueSpiritMage.png" : "RedSpiritMage.png";
            }
            before_pie = new ImageIcon(location).getImage();
            if (this.type == Terrain.LAKE && this.currentPiece.type == PieceType.WATER_MAGE || this.type == Terrain.MOUNTAIN && this.currentPiece.type == PieceType.AIR_MAGE || this.type == Terrain.FORREST && this.currentPiece.type == PieceType.EARTH_MAGE || this.type == Terrain.PLAINS && this.currentPiece.type == PieceType.FIRE_MAGE) {
                location = "GoodTerrain.png";
            } else if (this.type == Terrain.LAKE && this.currentPiece.type == PieceType.FIRE_MAGE || this.type == Terrain.MOUNTAIN && this.currentPiece.type == PieceType.WATER_MAGE || this.type == Terrain.FORREST && this.currentPiece.type == PieceType.AIR_MAGE || this.type == Terrain.PLAINS && this.currentPiece.type == PieceType.EARTH_MAGE) {
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

            Image after = combinedImage.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(after));
        } else {
            setIcon(new ImageIcon(before_ter.getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
        }
    }

}
