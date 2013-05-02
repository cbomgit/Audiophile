/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.media.player;

/**
 *
 * @author christian
 */

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


class PlaybackControl {
    
    private File             audioFile;     //current audio file being played
    private Thread           playbackThread;//thread that handles audio IO
    private SourceDataLine   nowPlaying;    // Line performing audio IO
    private AudioInputStream din;           //decoded input stream being read from
    private AudioFormat      decodedFormat; //decoded audio format of audioFile
    private volatile int     playerState;   //player's current state
    private PlaybackRunnable runnable;
    
    public PlaybackControl() {
       
    }
    
    public synchronized int getPlayerState() {
        return playerState;
    }
    
    public synchronized File getNowPlaying() {
        return audioFile;
    }
    
    private class PlaybackRunnable implements Runnable {

        @Override
        public void run() {
            
        }
        
    }
    
    
}
