package org.abalone.graphics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface AbaloneImages extends ClientBundle {
	@Source("images/EMPTY_BOARD.gif")
	ImageResource empty_board();
	
	@Source("images/BOARD_HIGHLIGHT.gif")
	ImageResource board_highlight();
	
	@Source("images/BOARD_WHITE.gif")
	ImageResource board_white();
	
	@Source("images/BOARD_WHITE_HIGHLIGHT.gif")
	ImageResource board_white_highlight();
	
	@Source("images/BOARD_RED.gif")
	ImageResource board_red();
	
	@Source("images/BOARD_RED_HIGHLIGHT.gif")
	ImageResource board_red_highlight();
	
	@Source("images/ILLEGAL_BOARD.gif")
	ImageResource illegal_board();
	
	@Source("images/RED_PIECE.png")
	ImageResource red_piece();
	
	@Source("images/RED_PIECE_HIGHLIGHT.png")
	ImageResource red_piece_highlight();
	
	@Source("images/WHITE_PIECE.png")
	ImageResource white_piece();
	
	@Source("images/WHITE_PIECE_HIGHLIGHT.png")
	ImageResource white_piece_highlight();
	
}
