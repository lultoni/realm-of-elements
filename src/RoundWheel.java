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
        int fontSize = 35;

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

        GridLayout lt = new GridLayout(1, 0);
        JPanel b2P = new JPanel(lt);
        JPanel b1P = new JPanel(lt);
        JPanel nP = new JPanel(lt);
        JPanel f1P = new JPanel(lt);
        JPanel f2P = new JPanel(lt);

        back2.setHorizontalAlignment(SwingConstants.CENTER);
        back1.setHorizontalAlignment(SwingConstants.CENTER);
        now.setHorizontalAlignment(SwingConstants.CENTER);
        forward1.setHorizontalAlignment(SwingConstants.CENTER);
        forward2.setHorizontalAlignment(SwingConstants.CENTER);

//        setBorder(BorderFactory.createEtchedBorder(new Color(243, 199, 23), new Color(197, 149, 98)));
        setBorder(BorderFactory.createEtchedBorder());
        b2P.setBorder(BorderFactory.createEtchedBorder());
        b1P.setBorder(BorderFactory.createEtchedBorder());
        nP.setBorder(BorderFactory.createEtchedBorder());
        f1P.setBorder(BorderFactory.createEtchedBorder());
        f2P.setBorder(BorderFactory.createEtchedBorder());

        Color background = new Color(114, 80, 63);
        setBackground(background);
        back2.setBackground(background);
        back1.setBackground(background);
        now.setBackground(background);
        forward1.setBackground(background);
        forward2.setBackground(background);
        b2P.setBackground(background);
        b1P.setBackground(background);
        nP.setBackground(background);
        f1P.setBackground(background);
        f2P.setBackground(background);

        JPanel filler1 = new JPanel();
        JPanel filler12 = new JPanel();
        filler1.setBackground(background);
        filler12.setBackground(background);
        b2P.add(filler1);
        b2P.add(back2);
        b2P.add(filler12);

        JPanel filler2 = new JPanel();
        JPanel filler22 = new JPanel();
        filler2.setBackground(background);
        filler22.setBackground(background);
        b1P.add(filler2);
        b1P.add(back1);
        b1P.add(filler22);

        JPanel filler3 = new JPanel();
        JPanel filler32 = new JPanel();
        filler3.setBackground(background);
        filler32.setBackground(background);
        nP.add(filler3);
        nP.add(now);
        nP.add(filler32);

        JPanel filler4 = new JPanel();
        JPanel filler42 = new JPanel();
        filler4.setBackground(background);
        filler42.setBackground(background);
        f1P.add(filler4);
        f1P.add(forward1);
        f1P.add(filler42);

        JPanel filler5 = new JPanel();
        JPanel filler52 = new JPanel();
        filler5.setBackground(background);
        filler52.setBackground(background);
        f2P.add(filler5);
        f2P.add(forward2);
        f2P.add(filler52);


        add(b2P);
        add(b1P);
        add(nP);
        add(f1P);
        add(f2P);
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
