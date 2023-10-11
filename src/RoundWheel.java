import javax.swing.*;
import java.awt.*;

public class RoundWheel extends JPanel {
    int round;
    GridLayout layout = new GridLayout(0, 1);
    JLabel back2 = new JLabel();
    JLabel back1 = new JLabel();
    JLabel now = new JLabel();
    JLabel forward1 = new JLabel();
    JLabel forward2 = new JLabel();
    public RoundWheel() {
        init();
    }

    public void setRound(int round) {
        this.round = round;
    }

    private void init() {
        updateText();
        setLayout(layout);
        int fontSize = 25;

        back2.setFont(new Font("Arial", Font.PLAIN, fontSize - 10));
        back1.setFont(new Font("Arial", Font.PLAIN, fontSize - 5));
        now.setFont(new Font("Arial", Font.BOLD, fontSize));
        forward1.setFont(new Font("Arial", Font.PLAIN, fontSize - 5));
        forward2.setFont(new Font("Arial", Font.PLAIN, fontSize - 10));

        Color c2 = new Color(117, 117, 117);
        Color c1 = new Color(173, 173, 173);
        Color cn = new Color(218, 218, 218);
        back2.setForeground(c2);
        back1.setForeground(c1);
        now.setForeground(cn);
        forward1.setForeground(c1);
        forward2.setForeground(c2);

        back2.setHorizontalAlignment(SwingConstants.CENTER);
        back1.setHorizontalAlignment(SwingConstants.CENTER);
        now.setHorizontalAlignment(SwingConstants.CENTER);
        forward1.setHorizontalAlignment(SwingConstants.CENTER);
        forward2.setHorizontalAlignment(SwingConstants.CENTER);

//        setBorder(BorderFactory.createEtchedBorder(new Color(243, 199, 23), new Color(197, 149, 98)));
        setBorder(BorderFactory.createEtchedBorder());
        back2.setBorder(BorderFactory.createEtchedBorder());
        back1.setBorder(BorderFactory.createEtchedBorder());
        now.setBorder(BorderFactory.createEtchedBorder());
        forward1.setBorder(BorderFactory.createEtchedBorder());
        forward2.setBorder(BorderFactory.createEtchedBorder());

        Color background = new Color(114, 80, 63);
        setBackground(background);
        back2.setBackground(background);
        back1.setBackground(background);
        now.setBackground(background);
        forward1.setBackground(background);
        forward2.setBackground(background);

        add(back2);
        add(back1);
        add(now);
        add(forward1);
        add(forward2);
    }

    public void updateText() {
        String text = ((round - 2) <= 0) ? " " : String.valueOf(round - 2);
        back2.setText(text);
        text = ((round - 1) <= 0) ? " " : String.valueOf(round - 1);
        back1.setText(text);
        text = String.valueOf(round);
        now.setText(text);
        text = String.valueOf(round + 1);
        forward1.setText(text);
        text = String.valueOf(round + 2);
        forward2.setText(text);
    }
}
