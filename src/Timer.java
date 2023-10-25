import javax.swing.*;
import java.awt.*;

public class Timer extends JPanel {

    int minutes;
    int seconds;
    int secondGain;
    JLabel timeLabel = new JLabel();
    boolean timerStopped;
    String name;
    Player player;
    GameHandler game;

    public Timer(Player player, int minutes, int secondGain, GameHandler game) {
        this.player = player;
        this.game = game;
        this.name = "null ";
        this.minutes = minutes;
        this.seconds = 0;
        this.secondGain = secondGain;
        this.timerStopped = false;
        setLayout(new GridLayout());
        setBorder(BorderFactory.createBevelBorder(0));
        update();
        add(timeLabel);
    }

    public void startTimer() {
        if (seconds - 60 >= 0) {
            seconds = seconds - 60;
            minutes++;
        }
        System.out.println(name + "Starting timer - " + timeLabel.getText());
        this.timerStopped = false;
        runTimer();
    }

    public void stopTimer(boolean gain) {
        if (gain) seconds += secondGain;
        if (seconds - 60 >= 0) {
            seconds = seconds - 60;
            minutes++;
        }
        System.out.println(name + "Stopping Timer - " + timeLabel.getText());
        timerStopped = true;
        update();
    }

    private void update() {
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 40));
        String time = ((minutes < 10) ? "0" + minutes : minutes) + ":" + ((seconds < 10) ? "0" + seconds : seconds);
        timeLabel.setText(time);
        Color isActive = new Color(119, 197, 135);
        Color isNotActive = new Color(124, 116, 116);
        Color isOver = new Color(243, 117, 117);
        setBackground((isTimerDone()) ? isOver : (timerStopped) ? isNotActive : isActive);
    }

    private void runTimer() {
        Thread timerFinishThread = new Thread(() -> {
            try {
                int flatSeconds = ((minutes * 60) + seconds);
                for (int i = 0; i <= flatSeconds; i++) {
                    if (timerStopped) {
                        break;
                    } else {
                        Thread.sleep(1000L);
                        if (!timerStopped) {
                            if (isTimerDone()) {
                                stopTimer(false);
                                game.getWinner(player);
                            } else if (seconds == 0) {
                                minutes--;
                                seconds = 59;
                            } else if (seconds - 61 >= 0) {
                                seconds = seconds - 61;
                                minutes++;
                            } else {
                                seconds--;
                            }
                        }
                    }
                    update();

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        timerFinishThread.start();
    }

    public boolean isTimerDone() {
        return minutes <= 0 && seconds <= 0;
    }

}
