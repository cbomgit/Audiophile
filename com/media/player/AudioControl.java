/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.media.player;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author christian
 */
class AudioControl {
    
    public static final int PLAYING = 0; //audio is being played back
    public static final int STOPPED = 1; //audio playback has been stopped
    public static final int PAUSED = 2; //audio playback has been paused
    public static final int DONE = 3; //audio playback has finished
    public static final int BUFFER_SIZE = 4096;
    
    private int playerState; 
    private SourceDataLine nowPlaying;
    private File soundFile;
    
    /* thread that governs audio playback. Everytime a new track is started
     * a new thread is spawned. 
     */
    private Thread playbackThread;
    
   
    private AudioIORunnable audioIORunnable;
    private AudioInputStream audioStream;
    
    AudioControl() {
        playerState = STOPPED;
    }
  
    
    private class AudioIORunnable implements Runnable {
        
        @Override
        public void run() {
            try {
                
                //decode the audio file
                AudioInputStream in = AudioSystem.getAudioInputStream(soundFile);
                AudioFormat baseFormat = in.getFormat();
                AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                                            baseFormat.getSampleRate(),
                                                            16,
                                                            baseFormat.getChannels(),
                                                            baseFormat.getChannels() * 2,
                                                            baseFormat.getSampleRate(),
                                                            false);
                
                audioStream = AudioSystem.getAudioInputStream(decodedFormat, in);
                // play it...
                playerState = PLAYING;
                writeAudioData(decodedFormat, audioStream);
                in.close();
            }
            catch(UnsupportedAudioFileException | LineUnavailableException | IOException e) {      
                stopPlayback();
            }
        }
        
        private synchronized void writeAudioData(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException {
            
            byte[] audioData = new byte[BUFFER_SIZE];
            
            //get and open a line for the formate specified
            getLine(targetFormat);
            
            if (nowPlaying != null) {
                // Start
                nowPlaying.start();
                int bytesRead = 0;
                bytesRead = din.read(audioData, 0, audioData.length);
                
                while (playerState != STOPPED && bytesRead != -1) {
                    
                    
                    if(playerState == PAUSED) {
                        try {
                            wait();
                        }
                        catch(InterruptedException e) {
                            System.out.println(e);
                        }
                    }
                    nowPlaying.write(audioData, 0, bytesRead);
                    bytesRead = din.read(audioData, 0, audioData.length);
                                                        
                }

                // Stop - discard all read data and close open line/stream
                nowPlaying.stop();
                nowPlaying.drain();
                nowPlaying.close();
                din.skip(din.available());
                din.close();
            }

        }
        
        private synchronized void getLine(AudioFormat audioFormat) throws LineUnavailableException {
            
            DataLine.Info info =
                    new DataLine.Info(SourceDataLine.class, audioFormat);
            
            nowPlaying = (SourceDataLine) AudioSystem.getLine(info);
            nowPlaying.open(audioFormat);
   
        }
    }
    
    public void play(File audioFile) {
        
        
        stopPlayback(); //stop the current track if it's playing
        soundFile = audioFile; //set the current file 

        //start a new thread with a new Runnable object
        audioIORunnable = new AudioIORunnable(); 
        try {
            if(playbackThread != null)
                playbackThread.join();
        }
        catch(InterruptedException e) {
            System.out.println(e);
        }
        finally {
            playbackThread = new Thread(audioIORunnable);
            playbackThread.start();
        }
        
    }
    
    public void stopPlayback() {
        
        //stop playback 
        if(playerState == PLAYING || playerState == PAUSED) {
            playerState = STOPPED;
            nowPlaying.stop();  
            nowPlaying.flush();
            soundFile = null;
        }
    }

    public void pause() {
        
        if(playerState == PLAYING) {
            playerState = PAUSED;
            nowPlaying.stop();
        }
    }
    
    public void resume() {
        final AudioIORunnable audioIO = audioIORunnable;
        
        if(playerState == PAUSED) {
            nowPlaying.start();
            
            synchronized(audioIO) {
                playerState = PLAYING;
                audioIO.notifyAll();
            }
        }
    }
    
    public void exit() {
        
        if(playbackThread != null && playbackThread.isAlive()) {
            playbackThread.interrupt();
            playbackThread = null;
        }
        
        if(audioStream != null) {
            
            try {
                audioStream.close();
            }
            catch(IOException e) {
                System.out.println(e);
            }
        }
    }
}
