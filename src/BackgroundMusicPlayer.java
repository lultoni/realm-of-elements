import java.util.ArrayList;
import java.util.Random;

public class BackgroundMusicPlayer {
    private final ArrayList<String> musicTracks;
    private final ArrayList<Integer> timeStamps;
    private final Random random = new Random();
    private int currentTrackIndex = -1;

    public BackgroundMusicPlayer() {
        musicTracks = new ArrayList<>();
        timeStamps = new ArrayList<>();
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

        // Ensure that the same track isn't played twice in a row.
        int newIndex;
        do {
            newIndex = random.nextInt(musicTracks.size());
        } while (newIndex == currentTrackIndex);

        currentTrackIndex = newIndex;
        String currentTrack = musicTracks.get(currentTrackIndex);
        int duration = timeStamps.get(currentTrackIndex);

        // Use your WAVPlayer class to play the track here.
        if (currentTrack.contains("music/")) {
            System.out.println("Playing Music: " + currentTrack.replaceFirst("music/", ""));
        } else {
            System.out.println("Playing: " + currentTrack);
        }
        WAVPlayer.play(currentTrack);

        // Simulate a track finish after the specified duration (in seconds).
        simulateTrackFinish(duration);
    }

    public void trackFinished() {
        // This method is called when the current track has finished playing.
        if (musicTracks.get(currentTrackIndex).contains("music/")) {
            System.out.println("Track finished: " + musicTracks.get(currentTrackIndex).replaceFirst("music/", ""));
        } else {
            System.out.println("Track finished: " + musicTracks.get(currentTrackIndex));
        }

        // Play the next random track after a short delay (e.g., 1 second).
        simulateNextTrackDelay();
    }

    private void simulateTrackFinish(int durationInSeconds) {
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

    private void simulateNextTrackDelay() {
        // Simulate a delay before playing the next track.
        Thread nextTrackDelayThread = new Thread(() -> {
            try {
                Thread.sleep(1000L); // Convert seconds to milliseconds
                playRandomTrack();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        nextTrackDelayThread.start();
    }

}
