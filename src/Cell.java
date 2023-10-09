import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Cell extends JButton {

    Terrain type;
    CellStatus status;
    int id;
    int timer;
    Piece currentPiece;

    public Cell(Terrain type, CellStatus status, int id) {
        this.type = type;
        this.status = status;
        this.id = id;
        this.timer = 0;
        this.currentPiece = null;
        // setText(String.valueOf(id));
        setFont(new Font("Arial", Font.BOLD, 20));
        updateIcon();
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
            int combinedWidth = before_ter.getWidth(null) + before_pie.getWidth(null);
            int combinedHeight = Math.max(before_ter.getHeight(null), before_pie.getHeight(null));
            BufferedImage combinedImage = new BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = combinedImage.createGraphics();
            g2d.drawImage(before_ter, 0, 0, null);
            g2d.drawImage(before_pie, before_ter.getWidth(null), 0, null);
            g2d.dispose();

            Image after = combinedImage.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(after));
        } else {
            setIcon(new ImageIcon(before_ter.getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
        }
    }

}
