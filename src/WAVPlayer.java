import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class WAVPlayer {
    private static SourceDataLine sourceDataLine; // Declare the SourceDataLine

    public static void play(String filePath) {
        File wavFile = new File(filePath);
        if (!wavFile.exists()) {
            System.out.println("The WAV file does not exist: " + filePath);
            return;
        }

        Thread audioThread = new Thread(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            playWAV(filePath);
        });
        audioThread.start();

        if (filePath.contains("music/")) {
            System.out.println("Playing Track " + filePath.replaceFirst("music/", "") + " in the background...");
        } else {
            System.out.println("Playing " + filePath + " in the background...");
        }
    }

    private static void playWAV(String wavFilePath) {
        Thread audioThread = new Thread(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            try {
                File file = new File(wavFilePath);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat audioFormat = audioInputStream.getFormat();
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
                SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

                byte[] buffer = new byte[8192]; // Increase buffer size

                int bytesRead;
                while ((bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                    sourceDataLine.write(buffer, 0, bytesRead);
                }

                sourceDataLine.drain();
                sourceDataLine.close();

            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        });
        audioThread.start();
    }

    public static void stop() {
        System.out.println("Stopping sound...");
        if (sourceDataLine != null) {
            Thread stopThread = new Thread(() -> {
                sourceDataLine.drain();
                sourceDataLine.close();
                sourceDataLine = null; // Set it to null to indicate that no audio is playing
            });
            stopThread.start();
        }
    }

    public static void stopAsync() {
        Thread stopThread = new Thread(WAVPlayer::stop);
        stopThread.start();
    }

}