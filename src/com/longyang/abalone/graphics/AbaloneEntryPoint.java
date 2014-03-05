package com.longyang.abalone.graphics;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.longyang.abalone.api.GameApi.Game;
import com.longyang.abalone.api.GameApi.IteratingPlayerContainer;
import com.longyang.abalone.api.GameApi.UpdateUI;
import com.longyang.abalone.api.GameApi.VerifyMove;
import com.longyang.abalone.impl.AbaloneLogic;
import com.longyang.abalone.impl.AbalonePresenter;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AbaloneEntryPoint implements EntryPoint {
  IteratingPlayerContainer container;
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
    container = new IteratingPlayerContainer(game, 2);
    AbaloneGraphics abaloneGraphics = new AbaloneGraphics();
    abalonePresenter =
        new AbalonePresenter(abaloneGraphics, container);
    FlowPanel flowPanel = new FlowPanel();
    flowPanel.add(abaloneGraphics);
    RootPanel.get("mainDiv").add(flowPanel);
    container.sendGameReady();
    container.updateUi(container.getPlayerIds().get(0));
  }
}