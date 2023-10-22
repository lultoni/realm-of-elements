public class Main {

    static volatile boolean isStartReady;

    public static void main (String[] args) {

        isStartReady = false;

        StartWindow startWindow = new StartWindow();
        while (!isStartReady) {
            Thread.onSpinWait();
        }
        GameHandler game = new GameHandler(startWindow.getPlayer1(), startWindow.getPlayer2());
        GameWindow gameWindow = new GameWindow(game);
        game.start();

    }

}
