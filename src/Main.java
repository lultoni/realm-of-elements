import javax.swing.*;

public class Main {
    static StartWindow startWindow;
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
        player.addTrack("A Bird Without Feathers.wav", 1, 58);
        player.addTrack("Age of Empires II Main Theme.wav", 1, 47);
        player.addTrack("Age of Empires Soundtrack - Track #8 - Egyptian Hymn.wav", 3, 11);
        player.addTrack("Beer and Women.wav", 1, 50);
        player.addTrack("Brotherhood of Bravery.wav", 1, 8);
        player.addTrack("Dream About Father.wav", 2, 49);
        player.addTrack("Drizzle.wav", 4, 8);
        player.addTrack("Felonious Junk.wav", 2, 48);
        player.addTrack("Finale.wav", 2, 10);
        player.addTrack("Floury Love.wav", 1, 16);
        player.addTrack("From Dusk to Sunrise.wav", 2, 8);
        player.addTrack("Get off My Band.wav", 3, 7);
        player.addTrack("Get Ye Sum.wav", 3, 17);
        player.addTrack("Good Luck, Son.wav", 1, 58);
        player.addTrack("Goodbye Brother.wav", 3, 7);
        player.addTrack("Introduction.wav", 3, 7);
        player.addTrack("Jons Honor.wav", 2, 31);
        player.addTrack("Kill Them All.wav", 2, 31);
        player.addTrack("Kitchen.wav", 1, 33);
        player.addTrack("Lady Steph and Henry.wav", 1, 52);
        player.addTrack("Landscape Atmosphere 20.wav", 1, 51);
        player.addTrack("Landscape Atmosphere 21.wav", 2, 31);
        player.addTrack("Landscape Atmosphere 22.wav", 2, 1);
        player.addTrack("Landscape Atmosphere 23.wav", 1, 37);
        player.addTrack("Landscape Atmosphere 25.wav", 2, 2);
        player.addTrack("Love In The Eyes.wav", 3, 54);
        player.addTrack("Machina del Diablo.wav", 4, 10);
        player.addTrack("Maps of the World.wav", 1, 58);
        player.addTrack("Of Licious.wav", 2, 47);
        player.addTrack("People of the Land.wav", 1, 57);
        player.addTrack("Rattay Feasts.wav", 1, 30);
        player.addTrack("Ready for Battle.wav", 2, 40);
        player.addTrack("Ride Lawrence Ride.wav", 4, 16);
        player.addTrack("Skalitz 1403.wav", 2, 18);
        player.addTrack("Tavern.wav", 1, 15);
        player.addTrack("The Kings Arrival.wav", 3, 30);
        player.addTrack("The Kingsroad.wav", 2, 2);
        player.addTrack("The Monkey Book.wav", 4, 25);
        player.addTrack("The River Sasau Theme.wav", 1, 47);
        player.addTrack("Winter Is Coming.wav", 2, 35);
        SwingUtilities.invokeLater(Main::showStartMenu);
    }

    public static void showStartMenu() {
        startWindow = new StartWindow(); // Assign the newly created StartWindow to the class variable
    }

    public static void play() {
        GameHandler game = new GameHandler(startWindow.getPlayer1(), startWindow.getPlayer2());
        player.game = game;
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
