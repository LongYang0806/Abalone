package org.abalone.sounds;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface GameSounds extends ClientBundle {
	
	@Source("pieceDown.mp3")
	DataResource pieceDownMp3();

	@Source("pieceDown.wav")
	DataResource pieceDownWav();
        
}