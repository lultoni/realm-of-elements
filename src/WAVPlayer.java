import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class WAVPlayer {

    static boolean isPlaying;

    public static void play(String filePath) {
//        isPlaying = true;

        File wavFile = new File(filePath);
        if (!wavFile.exists()) {
            System.out.println("The WAV file does not exist: " + filePath);
            return;
        }

        Thread audioThread = new Thread(() -> playWAV(filePath));
        audioThread.start();

        if (filePath.contains("music/")) {
            System.out.println("Playing Track " + filePath.replaceFirst("music/", "") + " in the background...");
        } else {
            System.out.println("Playing " + filePath + " in the background...");
        }
    }

    private static void playWAV(String wavFilePath) {
        try {
            File file = new File(wavFilePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat audioFormat = audioInputStream.getFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                if (!isPlaying) {
                    System.out.println("wait... i am doing what i am supposed to???");
                    break;
                }
                sourceDataLine.write(buffer, 0, bytesRead);
            }

            sourceDataLine.drain();
            sourceDataLine.close();

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        isPlaying = false;
    }
}