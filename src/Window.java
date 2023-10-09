import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    public Window() {

        GridLayout outerLayout = new GridLayout(1, 0);
        GridLayout pLayout = new GridLayout(0, 1);

        JPanel p1p = new JPanel(pLayout);
        JPanel tp = new JPanel(pLayout);
        JPanel p2p = new JPanel(pLayout);

        JLabel st1l = new JLabel("5");
        st1l.setHorizontalAlignment(SwingConstants.CENTER);
        st1l.setFont(new Font("Arial", Font.BOLD, 50));
        JLabel st2l = new JLabel("5");
        st2l.setHorizontalAlignment(SwingConstants.CENTER);
        st2l.setFont(new Font("Arial", Font.BOLD, 50));
        JButton st1u = new JButton("+");
        st1u.setFont(new Font("Arial", Font.BOLD, 50));
        st1u.addActionListener(e -> st1l.setText(String.valueOf(Integer.parseInt(st1l.getText()) + 1)));
        JButton st1d = new JButton("-");
        st1d.setFont(new Font("Arial", Font.BOLD, 50));
        st1d.addActionListener(e -> st1l.setText(String.valueOf(Integer.parseInt(st1l.getText()) - 1)));
        JButton st2u = new JButton("+");
        st2u.setFont(new Font("Arial", Font.BOLD, 50));
        st2u.addActionListener(e -> st2l.setText(String.valueOf(Integer.parseInt(st2l.getText()) + 1)));
        JButton st2d = new JButton("-");
        st2d.setFont(new Font("Arial", Font.BOLD, 50));
        st2d.addActionListener(e -> st2l.setText(String.valueOf(Integer.parseInt(st2l.getText()) - 1)));

        JLabel changer = new JLabel("1");
        changer.setHorizontalAlignment(SwingConstants.CENTER);
        changer.setFont(new Font("Arial", Font.BOLD, 30));
        JLabel turnCounter = new JLabel("1");
        turnCounter.setHorizontalAlignment(SwingConstants.CENTER);
        turnCounter.setFont(new Font("Arial", Font.BOLD, 50));
        JButton tu = new JButton("Next Round");
        tu.setFont(new Font("Arial", Font.BOLD, 20));
        tu.addActionListener(e -> {
            turnCounter.setText(String.valueOf(Integer.parseInt(turnCounter.getText()) + 1));
            if (Integer.parseInt(turnCounter.getText()) % 5 == 0) {
                changer.setText(String.valueOf(Integer.parseInt(changer.getText()) + 1));
            }
            st1l.setText(String.valueOf(Integer.parseInt(st1l.getText()) + Integer.parseInt(changer.getText())));
            st2l.setText(String.valueOf(Integer.parseInt(st2l.getText()) + Integer.parseInt(changer.getText())));
        });

        setLayout(outerLayout);
        setBounds(10, 10, 500, 500);
        setTitle("Realm of Elements Helper");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        p1p.add(st1u);
        p1p.add(st1l);
        p1p.add(st1d);
        add(p1p);
        tp.add(changer);
        tp.add(turnCounter);
        tp.add(tu);
        add(tp);
        p2p.add(st2u);
        p2p.add(st2l);
        p2p.add(st2d);
        add(p2p);


    }

    public static void main (String[] args) {

        Window window = new Window();

    }

}
