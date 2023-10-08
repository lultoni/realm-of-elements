import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    private final GridLayout outerLayout = new GridLayout(1, 0);
    private final GridLayout boardLayout = new GridLayout(8, 0);
    private final JPanel boardPanel = new JPanel();
    private final JPanel controlPanel = new JPanel();

    public GameWindow() {
        setBounds(10, 10, 1000, 650);
        setTitle("Realm of Elements");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
        setVisible(true);
    }

    private void init() {
        setLayout(outerLayout);
        boardPanel.setLayout(boardLayout);

        for (int i = 0; i < 64; i++) {
            boardPanel.add(new JLabel(String.valueOf(i)));
        }

        add(boardPanel);
        add(controlPanel);
    }

}
