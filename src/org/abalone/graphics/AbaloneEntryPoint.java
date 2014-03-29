package org.abalone.graphics;

import org.game_api.GameApi.ContainerConnector;
import org.game_api.GameApi.Game;
import org.game_api.GameApi.UpdateUI;
import org.game_api.GameApi.VerifyMove;
import org.abalone.impl.AbaloneLogic;
import org.abalone.impl.AbalonePresenter;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AbaloneEntryPoint implements EntryPoint {
	ContainerConnector container;
  AbalonePresenter abalonePresenter;

  @Override
  public void onModuleLoad() {
    Game game = new Game() {
      @Override
      public void sendVerifyMove(VerifyMove verifyMove) {
        container.sendVerifyMoveDone(new AbaloneLogic().verify(verifyMove));
      }

      @Override
      public void sendUpdateUI(UpdateUI updateUI) {
        abalonePresenter.updateUI(updateUI);
      }
    };
    
    container = new ContainerConnector(game);
    AbaloneGraphics abaloneGraphics = new AbaloneGraphics();
    abalonePresenter = new AbalonePresenter(abaloneGraphics, container);
    FlowPanel flowPanel = new FlowPanel();
    flowPanel.add(abaloneGraphics);
    // This is the DOM and main logic binding.
    RootPanel.get("mainDiv").add(flowPanel);
    container.sendGameReady();
  }
}