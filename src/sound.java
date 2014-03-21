import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class sound
{
	
	
	public void playSound(String s) throws Exception
	{
		/*
		  Clip clip = AudioSystem.getClip();
		  AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(s));
		  clip.open(inputStream);
	      clip.start(); 
	      		
	      		*/
		  Clip clip = AudioSystem.getClip();
		  InputStream audioSrc = getClass().getResourceAsStream(s);
		  InputStream bufferedIn = new BufferedInputStream(audioSrc);
		  AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);	  
		  clip.open(inputStream);
	      clip.start(); 
	  


	      
	}
}