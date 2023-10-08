import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    private GameHandler game;
    private final GridLayout outerLayout = new GridLayout(1, 0);
    private final GridLayout boardLayout = new GridLayout(8, 0);
    private final JPanel boardPanel = new JPanel();
    private final JPanel controlPanel = new JPanel();

    public GameWindow(GameHandler game) {
        this.game = game;
        int width = 1000;
        setBounds(10, 10, width, (int) (width/1.85));
        setTitle("Realm of Elements");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
        setVisible(true);
    }

    private void init() {
        setLayout(outerLayout);
        boardPanel.setLayout(boardLayout);

        for (int i = 0; i < 64; i++) {
            boardPanel.add(game.board[i]);
        }

        add(boardPanel);
        add(controlPanel);
    }

}
