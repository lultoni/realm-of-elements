import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Cell extends JButton {

    Terrain type;
    CellStatus status;
    int id;
    int timer;
    Piece currentPiece;
    GameHandler game;

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
            boolean canMove = (status == CellStatus.OCCUPIED && !currentPiece.hasMoved && (game.turn == TurnState.P1MOVEMENT && currentPiece.isBlue || game.turn == TurnState.P2MOVEMENT && !currentPiece.isBlue) || status == CellStatus.OPEN && game.selectedPiece != null && !game.selectedPiece.hasMoved && (game.turn == TurnState.P1MOVEMENT && game.selectedPiece.isBlue || game.turn == TurnState.P2MOVEMENT && !game.selectedPiece.isBlue));
            System.out.println("sel op1 = " + (status == CellStatus.OCCUPIED));
            System.out.println("sel op2 = " + (game.selectedPiece == null));
            System.out.println("mov op1 = " + (status == CellStatus.OPEN));
            System.out.println("mov op2 = " + (game.selectedPiece != null));
            System.out.println("can = " + canMove);
            if (canMove && ((game.turn == TurnState.P1MOVEMENT && game.player1.movementCounter > 0) || (game.turn == TurnState.P2MOVEMENT && game.player2.movementCounter > 0))) {
                if (status == CellStatus.OCCUPIED && game.selectedPiece == null) {
                    System.out.println("Select Piece");
                    if (!currentPiece.hasMoved) {
                        game.selectedPiece = currentPiece;
                        game.fromID = id;
                        System.out.println("Done");
                    }
                } else if (status == CellStatus.OPEN && game.selectedPiece != null) {
                    System.out.println("Move Piece");
                    currentPiece = game.selectedPiece;
                    currentPiece.hasMoved = true;
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
                }
            }
        });
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
        switch (this.type) {
            case LAKE -> location = "LakeSprite.png";
            case MOUNTAIN -> location = "MountainSprite.png";
            case FORREST -> location = "ForrestSprite.png";
            case PLAINS -> location = "PlainsSprite.png";
        }
        before_ter = new ImageIcon(location).getImage();
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
            int combinedWidth = (before_ter.getWidth(null) + before_pie.getWidth(null))/2;
            int combinedHeight = (before_ter.getHeight(null) + before_pie.getHeight(null))/2;
            BufferedImage combinedImage = new BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = combinedImage.createGraphics();
            g2d.drawImage(before_ter, 0, 0, null);
            g2d.drawImage(before_pie, 0, 0, null);
            g2d.dispose();

            Image after = combinedImage.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(after));
        } else {
            setIcon(new ImageIcon(before_ter.getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
        }
    }

}
