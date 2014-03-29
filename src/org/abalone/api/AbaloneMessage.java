package org.abalone.api;

import org.abalone.impl.AbalonePresenter;

/**
 * Enum is used to present the message which can reflect the game status to player
 * And this is passed from {@link AbalonePresenter} to {@link View}
 *
 * @author Long Yang (ly603@nyu.edu)
 *
 */
public enum AbaloneMessage {
	UNDERGOING, GAMEOVER
}
