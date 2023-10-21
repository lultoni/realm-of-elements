import java.awt.*;
import javax.swing.JPanel;

public class EvaluationBar extends JPanel {
    private int evaluation;
    private int newSplitY;
    private int oldSplitY;
    private int currentSplitY;
    private int height;
    private final int animationDuration;
    private long startTime;
    private boolean isAnimationFinished;
    public boolean shouldAnimate;
    private final int maxValue;

    public EvaluationBar(int evaluation) {
        this.evaluation = evaluation;
        this.newSplitY = 0;
        this.oldSplitY = 0;
        this.currentSplitY = 0;
        this.animationDuration = 1000;
        this.startTime = -1;
        this.shouldAnimate = false;
        this.maxValue = 300;
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
        height = getHeight();

        // Calculate the minimum width based on 10% of the height
        int minWidth = (int) (height * 0.05);

        if (width < minWidth) {
            width = minWidth;
            setPreferredSize(new Dimension(width, height));
        }

        Color p1col = new Color(60, 126, 176);
        Color p2col = new Color(164, 53, 53);

        if (shouldAnimate) {
            isAnimationFinished = false;
            startAnimation();
        } else {
            calculations();
            currentSplitY = newSplitY;
        }

        // Fill the top part with player 2 color (e.g., red)
        g.setColor(p2col); // Change to your player 2 color
        g.fillRect(0, 0, width, currentSplitY);

        // Fill the bottom part with player 1 color (e.g., blue)
        g.setColor(p1col); // Change to your player 1 color
        g.fillRect(0, currentSplitY, width, height - currentSplitY);

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

    public void startAnimation() {
        if (startTime == -1) {
            startTime = System.currentTimeMillis();
            calculations();
        }
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        if (elapsedTime < animationDuration) {
            double t = (double) elapsedTime / animationDuration;
            double easingValue = cubicBezierEaseInOut(t);

            currentSplitY = (int) (oldSplitY + (newSplitY - oldSplitY) * easingValue);

            repaint();
        } else {
            startTime = -1;
            isAnimationFinished = true;
            shouldAnimate = false;
        }
    }

    private void calculations() {
        // Calculate the percentage based on the evaluation
        double percentage = 1 - (double) (evaluation + maxValue) / (2 * maxValue); // Normalize to the range [0, 1]
        // Calculate the position where the split line should be based on the percentage
        oldSplitY = newSplitY; // Start split
        newSplitY = (int) (height * percentage); // End split
    }

    private double cubicBezierEaseInOut(double x) {
        return x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2;
    }

}