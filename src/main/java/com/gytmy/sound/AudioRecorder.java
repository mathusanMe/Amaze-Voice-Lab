package com.gytmy.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.util.ArrayList;
import java.util.List;

/**
 * SoundRecorder is a class that records sound from a microphone and saves it
 * into a WAV file. It follows the singleton pattern.
 * 
 * @see :
 *      https://www.codejava.net/coding/capture-and-record-sound-into-wav-file-with-java-sound-api
 */
public class AudioRecorder {

    // We want the format of our files to be WAV
    private static final AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;
    private static final long DEFAULT_MAX_RECORD_DURATION_MILLISECONDS = 11000;

    private Thread stopper;
    private File wavFile; // The file that will store the recorded sound
    private static List<RecordObserver> observers = new ArrayList<>();

    // A TargetDataLine represents a mono or multi-channel audio feed
    // from which audio data can be read.
    private TargetDataLine channel;

    private static AudioRecorder instance;

    public static AudioRecorder getInstance() {
        if (instance == null) {
            instance = new AudioRecorder();
        }
        return instance;
    }

    private AudioRecorder() {

    }

    public void start(String audioFilePath) {
        start(audioFilePath, getTotalDurationInSeconds());
    }

    /**
     * Opens the channel and starts recording
     */
    public void start(String audioFilePath, int durationInSeconds) {
        initFile(audioFilePath);
        initiateStopper(durationInSeconds);

        try {
            openChannel();
            stopper.start();
            captureAndRecord(channel);

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
            System.out.println("Error: Line Unavailable");
        }
    }

    private void initFile(String audioFilePath) {
        this.wavFile = new File(audioFilePath);

        File parent = wavFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        // Make sure the file exists
        if (!wavFile.exists()) {
            try {
                wavFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: File cannot be created");
            }
        }
    }

    /**
     * Creates a new thread that waits for a specified delay before stopping record
     * 
     * @return Thread object
     */
    private void initiateStopper(long durationInSeconds) {
        stopper = new Thread(() -> {
            try {
                Thread.sleep(durationInSeconds * 1000 + 100);
            } catch (InterruptedException ex) {
            }
            if (channel != null && channel.isOpen()) {
                finish();
            }
        });
    }

    /**
     * Open the channel to capture the sound
     * 
     * @throws LineUnavailableException If the channel is not available
     */
    private void openChannel() throws LineUnavailableException {
        AudioFormat format = getAudioFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        // checks if system supports the data line
        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Error: Line Not Supported In Its Current Format");
        }

        channel = (TargetDataLine) AudioSystem.getLine(info);
        channel.open(format);
        channel.start(); // start capturing
    }

    /**
     * Defines an audio format
     * We want the format to be 16kHz, 8-bit and mono
     */
    private static AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

    /**
     * Capture the sound and record it into a WAV file
     * 
     * @param targetLine
     */
    private void captureAndRecord(TargetDataLine targetLine) {

        AudioInputStream inputStream = capture(targetLine);
        record(inputStream);
    }

    /**
     * The act of capture a sound is the process of transform a sound channel into
     * readable data, which is a audio input stream.
     * 
     * @param targetLine Audio Input Stream
     * @return Audio input stream that reads its data from targetLine
     */
    private AudioInputStream capture(TargetDataLine targetLine) {

        return new AudioInputStream(targetLine);
    }

    /**
     * Read the audio input stream and write it into a file
     * 
     * @param inputStream Audio input stream
     */
    private void record(AudioInputStream inputStream) {

        new Thread(() -> {
            try {
                AudioSystem.write(inputStream, FILE_TYPE, wavFile);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: File was not found");
            }
        }).start();
    }

    public static boolean isRecording() {
        AudioRecorder instance = getInstance();
        return instance.stopper != null && instance.stopper.getState() == Thread.State.TIMED_WAITING;
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    public void finish() {
        if (channel != null) {
            channel.stop();
            channel.close();
        }
        if (stopper != null) {
            stopper.interrupt();
        }
        if (channel != null) {
            notifyEndToObservers();
        }
    }

    public static int getTotalDurationInSeconds() {
        return (int) ((DEFAULT_MAX_RECORD_DURATION_MILLISECONDS - 100) / 1000);
    }

    public static void notifyStartToObservers() {
        observers.forEach(RecordObserver::startRecordUpdate);
    }

    public static void notifyEndToObservers() {
        observers.forEach(RecordObserver::endRecordUpdate);
    }

    public static void addObserver(RecordObserver observer) {
        observers.add(observer);
    }

    public static void removeObserver(RecordObserver observer) {
        observers.remove(observer);
    }
}