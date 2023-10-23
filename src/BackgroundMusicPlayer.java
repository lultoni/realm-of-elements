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
        WAVPlayer.isPlaying = true;
        WAVPlayer.play(currentTrack);

        simulateTrackFinish(duration); // TODO break timer of track
    }

    public void trackFinished() {
        if (musicTracks.get(currentTrackIndex).contains("music/")) {
            System.out.println("Track finished: " + musicTracks.get(currentTrackIndex).replaceFirst("music/", ""));
        } else {
            System.out.println("Track finished: " + musicTracks.get(currentTrackIndex));
        }

        simulateNextTrackDelay();
    }

    private void simulateTrackFinish(int durationInSeconds) {
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
            WAVPlayer.stop();
        }
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
