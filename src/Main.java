import javax.swing.*;

public class Main {
    private static StartWindow startWindow; // Declare startWindow as a class variable
    public static BackgroundMusicPlayer player;

    public static void main(String[] args) {
        player = new BackgroundMusicPlayer();
        player.addTrack("Arabia (The Medieval Era).wav", 3, 26);
        player.addTrack("Aztec (The Medieval Era).wav", 2, 44);
        player.addTrack("Camelot.wav", 4, 19);
        player.addTrack("Charge Of The Knights.wav", 5, 5);
        player.addTrack("Crusader Kings 2 Main Title (From the Crusader Kings 2 Original Game Soundtrack).wav", 2, 47);
        player.addTrack("Crusaders (From the Crusader Kings 2 Original Game Soundtrack).wav", 4, 10);
        player.addTrack("Dusking Sky Pt. 1.wav", 5, 28);
        player.addTrack("Dusking Sky Pt. 2.wav", 5, 40);
        player.addTrack("Egypt (The Medieval Era).wav", 3, 10);
        player.addTrack("England (The Medieval Era).wav", 4, 9);
        player.addTrack("France (The Medieval Era).wav", 3, 13);
        player.addTrack("Greece (The Medieval Era).wav", 3, 13);
        player.addTrack("In Taberna Revisited.wav", 3, 5);
        player.addTrack("Journey To Absolution (From the Crusader Kings 2 Original Game Soundtrack).wav", 3, 17);
        player.addTrack("Knights Of Jerusalem.wav", 5, 21);
        player.addTrack("Kongo (The Medieval Era).wav", 3, 43);
        player.addTrack("Krak Des Chevaliers (From the Crusader Kings 2 Original Game Soundtrack).wav", 5, 31);
        player.addTrack("Liement me deport.wav", 2, 13);
        player.addTrack("March To Holyland (From the Crusader Kings 2 Original Game Soundtrack).wav", 3, 15);
        player.addTrack("Northwind.wav", 9, 39);
        player.addTrack("Path To Glory (From the Crusader Kings 2 Original Game Soundtrack).wav", 3, 2);
        player.addTrack("Pilgrimage (From the Crusader Kings 2 Original Game Soundtrack).wav", 1, 45);
        player.addTrack("Prophecy.wav", 5, 34);
        player.addTrack("Reverse Dance.wav", 3, 38);
        player.addTrack("Rome (The Medieval Era).wav", 3, 38);
        player.addTrack("Russia (The Medieval Era).wav", 3, 57);
        player.addTrack("Seaside Tavern.wav", 3, 53);
        player.addTrack("Spain (The Medieval Era).wav", 3, 48);
        player.addTrack("The Banquet.wav", 3, 20);
        player.addTrack("The Dynasty.wav", 5, 12);
        player.addTrack("The First Crusade (From the Crusader Kings 2 Original Game Soundtrack).wav", 4, 58);
        player.addTrack("Veni Vidi Vici.wav", 4, 12);
        player.addTrack("Winds of Ithaca.wav", 6, 6);
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
