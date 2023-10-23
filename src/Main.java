import javax.swing.*;

public class Main {
    private static StartWindow startWindow; // Declare startWindow as a class variable
    public static volatile boolean isStartReady;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showStartMenu);
    }

    public static void showStartMenu() {
        startWindow = new StartWindow(); // Assign the newly created StartWindow to the class variable
    }

    public static void play() {
        GameHandler game = new GameHandler(startWindow.getPlayer1(), startWindow.getPlayer2());
        GameWindow gameWindow = new GameWindow(game);
        gameWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                System.out.println("Game window closed.");
            }
        });
        game.start();
    }
}
