package org.abalone.i18n;

import com.google.gwt.i18n.client.Messages;

public interface AbaloneMessages extends Messages{
	@DefaultMessage("Red Player")
	String redPlayer();
	
	@DefaultMessage("White Player")
	String whitePlayer();
	
	@DefaultMessage("Viewer")
	String viewer();
	
	@DefaultMessage("Finish This Round")
	String finishThisRound();
	
	@DefaultMessage("Game Over and the winner is: {0}")
	String gameOver(String winner);
	
	@DefaultMessage("OK")
	String ok();
}
