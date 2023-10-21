import java.awt.*;
import javax.swing.JPanel;

public class EvaluationBar extends JPanel {
    private int evaluation;

    public EvaluationBar(int evaluation) {
        this.evaluation = evaluation;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
        revalidate(); // Trigger a layout update
        repaint(); // Redraw the bar when the evaluation changes
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        // Calculate the minimum width based on 10% of the height
        int minWidth = (int) (height * 0.05);

        if (width < minWidth) {
            width = minWidth;
            setPreferredSize(new Dimension(width, height));
        }

        // Calculate the percentage based on the evaluation
        int maxValue = 300;
        double percentage = 1 - (double) (evaluation + maxValue) / (2 * maxValue); // Normalize to the range [0, 1]

        // Calculate the position where the split line should be based on the percentage
        int splitY = (int) (height * percentage);

        Color p1col = new Color(60, 126, 176);
        Color p2col = new Color(164, 53, 53);

        // Fill the top part with player 2 color (e.g., red)
        g.setColor(p2col); // Change to your player 2 color
        g.fillRect(0, 0, width, splitY);

        // Fill the bottom part with player 1 color (e.g., blue)
        g.setColor(p1col); // Change to your player 1 color
        g.fillRect(0, splitY, width, height - splitY);

        // Display the evaluation as text
        g.setColor(Color.BLACK); // Change text color
        String evalText = (evaluation == 10000) ? "0-1" : (evaluation == -10000) ? "1-0" : String.valueOf(evaluation);
        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        FontMetrics fontMetrics = g.getFontMetrics();
        int textX = (width - fontMetrics.stringWidth(evalText)) / 2;
        int textY = height / 2 + fontMetrics.getAscent();
        g.drawString(evalText, textX, textY);
    }
}