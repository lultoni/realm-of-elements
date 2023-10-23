import java.util.ArrayList;
import java.util.Random;

public class BackgroundMusicPlayer {
    private static final ArrayList<String> musicTracks = new ArrayList<>();
    private static final ArrayList<Integer> timeStamps = new ArrayList<>();
    private static final Random random = new Random();
    private static int currentTrackIndex = -1;
    private static boolean isPlaying = false;

    public static void addTrack(String trackName, int minutes, int seconds) {
        String trackPath = "music/" + trackName;
        musicTracks.add(trackPath);

        int time = minutes * 60 + seconds;
        timeStamps.add(time);
    }

    public static void playRandomTrack() {
        if (musicTracks.isEmpty()) {
            System.out.println("No music tracks available.");
            return;
        }

//        if (isPlaying) {
//            // If music is already playing, stop the current track.
//            stopMusic();
//        }

        // Ensure that the same track isn't played twice in a row.
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

        isPlaying = true;
        WAVPlayer.play(currentTrack);

        // Simulate a track finish after the specified duration (in seconds).
        simulateTrackFinish(duration);
    }

    public static void stopMusic() {
        // If music is playing, stop it.
        if (isPlaying) {
//            WAVPlayer.stop(); // Replace with appropriate method to stop music.
            isPlaying = false;
        }
    }

    public static void nextTrack() {
        // Stop the current track and play the next one.
//        stopMusic();
        playRandomTrack();
    }

    public static void trackFinished() {
        // This method is called when the current track has finished playing.
        if (musicTracks.get(currentTrackIndex).contains("music/")) {
            System.out.println("Track finished: " + musicTracks.get(currentTrackIndex).replaceFirst("music/", ""));
        } else {
            System.out.println("Track finished: " + musicTracks.get(currentTrackIndex));
        }

        // Play the next random track after a short delay (e.g., 0.5 seconds).
        simulateNextTrackDelay();
    }

    private static void simulateTrackFinish(int durationInSeconds) {
        // Simulate track finish after the specified duration.
        Thread trackFinishThread = new Thread(() -> {
            try {
                Thread.sleep(durationInSeconds * 1000L); // Convert seconds to milliseconds
                trackFinished();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        trackFinishThread.start();
    }

    private static void simulateNextTrackDelay() {
        // Simulate a delay before playing the next track.
        Thread nextTrackDelayThread = new Thread(() -> {
            try {
                Thread.sleep(500L); // Convert seconds to milliseconds
                nextTrack();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        nextTrackDelayThread.start();
    }
}
