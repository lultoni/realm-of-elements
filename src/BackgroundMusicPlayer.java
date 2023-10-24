import java.util.ArrayList;
import java.util.Random;

public class BackgroundMusicPlayer {
    private final ArrayList<String> musicTracks;
    private final ArrayList<Integer> timeStamps;
    private final Random random = new Random();
    private int currentTrackIndex = -1;
    boolean skipNextTrack;
    GameHandler game;

    public BackgroundMusicPlayer() {
        musicTracks = new ArrayList<>();
        timeStamps = new ArrayList<>();
        skipNextTrack = false;
    }

    public void addTrack(String trackName, int minutes, int seconds) {

        String trackPath = "music/" + trackName;
        musicTracks.add(trackPath);

        int time = minutes * 60 + seconds;
        timeStamps.add(time);
    }



    public void playRandomTrack() {
        if (musicTracks.isEmpty()) {
            System.out.println("No music tracks available.");
            return;
        }

        int newIndex;
        do {
            newIndex = random.nextInt(musicTracks.size());
        } while (newIndex == currentTrackIndex);

        currentTrackIndex = newIndex;
        String currentTrack = musicTracks.get(currentTrackIndex);
        int duration = timeStamps.get(currentTrackIndex);

        if (currentTrack.contains("music/")) {
            System.out.println("Playing Music: " + currentTrack.replaceFirst("music/", ""));
        } else {
            System.out.println("Playing: " + currentTrack);
        }
        if (Main.startWindow != null) Main.startWindow.updateSong();
        if (game != null) game.window.updateSong();
        WAVPlayer.isPlaying = true;
        WAVPlayer.play(currentTrack);

        simulateTrackFinish(duration);
    }

    public void trackFinished() {
        if (musicTracks.get(currentTrackIndex).contains("music/")) {
            System.out.println("Track finished: " + musicTracks.get(currentTrackIndex).replaceFirst("music/", ""));
        } else {
            System.out.println("Track finished: " + musicTracks.get(currentTrackIndex));
        }

        if (!skipNextTrack) {
            simulateNextTrackDelay();
        }
        skipNextTrack = false;
    }

    private void simulateTrackFinish(int durationInSeconds) {
        Thread trackFinishThread = new Thread(() -> {
            try {
                for (int i = 0; i <= durationInSeconds; i++) {
                    if (skipNextTrack) {
                        System.out.println("Stopping Background Sleeper.");
                        break;
                    } else {
                        Thread.sleep(1000L); // Convert seconds to milliseconds
                    }

                }
                trackFinished();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        trackFinishThread.start();
    }

    private void simulateNextTrackDelay() {
        Thread nextTrackDelayThread = new Thread(() -> {
            try {
                Thread.sleep(500L);
                playRandomTrack();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        nextTrackDelayThread.start();
    }

    public void stopMusic() {
        if (WAVPlayer.isPlaying) {
            System.out.println("Stopping Music...");
            skipNextTrack = true;
            WAVPlayer.stop();
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException b) {
            b.printStackTrace();
        }
        WAVPlayer.isPlaying = true;
        System.out.println("Enabling Sounds to be played.");
    }

    public String getTrackName() {
        String out = musicTracks.get(currentTrackIndex);
        if (out.contains("music/")) {
            out = out.replaceFirst("music/", "");
        }
        if (out.contains(".wav")) {
            out = out.replaceFirst(".wav", "");
        }
        return out;
    }
}
