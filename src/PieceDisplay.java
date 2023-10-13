import javax.swing.*;
import java.awt.*;

public class PieceDisplay extends JPanel {

    Piece[] pieces;
    FlowLayout layout = new FlowLayout(FlowLayout.LEFT);

    public PieceDisplay() {
        init();
    }

    private void init() {
        setLayout(layout);
        updateCaptures();
    }

    public void updateCaptures() {
        Component[] componentList = getComponents();
        for(Component c : componentList){
            if(c instanceof JLabel){
                remove(c);
            }
        }
        revalidate();
        repaint();
        if (pieces != null) for (Piece piece: pieces) {
            if (piece.cellID == -1) {
                JLabel fakeImage = new JLabel();
                String location = "";
                Image icon;
                switch (piece.type) {
                    case GUARD -> location = (piece.isBlue) ? "BlueGuard.png" : "RedGuard.png";
                    case AIR_MAGE -> location = (piece.isBlue) ? "BlueAirMage.png" : "RedAirMage.png";
                    case FIRE_MAGE -> location = (piece.isBlue) ? "BlueFireMage.png" : "RedFireMage.png";
                    case EARTH_MAGE -> location = (piece.isBlue) ? "BlueEarthMage.png" : "RedEarthMage.png";
                    case WATER_MAGE -> location = (piece.isBlue) ? "BlueWaterMage.png" : "RedWaterMage.png";
                    case SPIRIT_MAGE -> location = (piece.isBlue) ? "BlueSpiritMage.png" : "RedSpiritMage.png";
                }
                icon = new ImageIcon(location).getImage();
                fakeImage.setIcon(new ImageIcon(icon.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
                add(fakeImage);
            }
        }
    }
}
