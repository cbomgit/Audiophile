
package com.media.player;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author christian
 */
public class Main {
    
    public static final String SAMPLE_WAV = "/home/christian/music/Pink Floyd/rare/Unknown Album/";
    public static final String SAMPLE_MP3 = "/home/christian/music/Tool/lateralus/";
    
    public static void main(String [] args) throws InterruptedException, UnsupportedAudioFileException, IOException {
        
        
                
        File root = new File(SAMPLE_MP3);
        File [] rootContents = root.listFiles();
        
        int numSongs = 0;
        for (int i = 0; i < rootContents.length; i++)
            if(rootContents[i].getAbsolutePath().endsWith(".mp3"))
                numSongs++;
        
        File [] playQueue = new File[numSongs];
        
        for (int i = 0, j = 0; i < rootContents.length; i++)
            if(rootContents[i].getAbsolutePath().endsWith(".mp3"))
                playQueue[j++] = rootContents[i];
        
        for(int i = 0; i < playQueue.length; i++) 
            System.out.println(playQueue[i].getAbsoluteFile());
        
        Scanner in = new Scanner(System.in);
        String input = "";
        int queuePosition = 0;
        System.out.println("Here");
        AudioControl player = new AudioControl();
        
        System.out.print(":");
        if(in.hasNextLine())
            input = in.nextLine();
        
        while(!"exit".equals(input)) {
            
                        
            if(input.equals("play")) {
                player.play(playQueue[queuePosition]);
                System.out.println("Now playing : ");
                System.out.println(playQueue[queuePosition].getName());
            }
            else if(input.equals("next")) {
                player.play(playQueue[++queuePosition]);
                System.out.println("Now playing : ");
                System.out.println(playQueue[queuePosition].getName());
            }
            else if(input.equals("prev")) {
                player.play(playQueue[--queuePosition]);
                System.out.println("Now playing : ");
                System.out.println(playQueue[queuePosition].getName());
            }
            else if(input.equals("stop")) {
                player.stopPlayback();
                System.out.println("stopped playback");
            }
            else if(input.equals("pause")) {
                player.pause();
                System.out.println("paused playback");
            }
            else if(input.equals("resume")) {
                player.resume();
                System.out.println("unpaused playback");
            }
            else
                System.out.println("Unknown command");
            
            System.out.print(":");
            if(in.hasNextLine())
                input = in.nextLine();
        }
        
        player.exit();
        
    }
}
