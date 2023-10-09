import javax.swing.*;
import java.awt.*;

public class Cell extends JButton {

    Terrain type;
    CellStatus status;
    int id;
    int timer;

    public Cell(Terrain type, CellStatus status, int id) {
        this.type = type;
        this.status = status;
        this.id = id;
        this.timer = 0;
        // setText(String.valueOf(id));
        setFont(new Font("Arial", Font.BOLD, 20));
        Image before = null;
        switch (this.type) {
            case LAKE -> before = new ImageIcon("LakeSprite.png").getImage();
            case MOUNTAIN -> before = new ImageIcon("MountainSprite.png").getImage();
            case FORREST -> before = new ImageIcon("ForrestSprite.png").getImage();
            case PLAINS -> before = new ImageIcon("PlainsSprite.png").getImage();
        }
        Image after = before.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(after));
    }

}
