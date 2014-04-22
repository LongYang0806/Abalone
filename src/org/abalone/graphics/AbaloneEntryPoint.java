package org.abalone.graphics;

import org.abalone.client.AbaloneLogic;
import org.abalone.client.AbalonePresenter;
import org.game_api.GameApi;
import org.game_api.GameApi.Game;
import org.game_api.GameApi.IteratingPlayerContainer;
import org.game_api.GameApi.UpdateUI;
import org.game_api.GameApi.VerifyMove;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AbaloneEntryPoint implements EntryPoint {
	IteratingPlayerContainer container;
// 	ContainerConnector container;
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
//		container = new ContainerConnector(game);
		AbaloneGraphics abaloneGraphics = new AbaloneGraphics();
		abalonePresenter = new AbalonePresenter(abaloneGraphics, container);
		
		final ListBox playerSelect = new ListBox();
		playerSelect.addItem("White Player");
		playerSelect.addItem("Red Player");
		playerSelect.addItem("Viewer");
		playerSelect.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int selectedIndex = playerSelect.getSelectedIndex();
				String playerId = selectedIndex == 2 ? GameApi.VIEWER_ID : 
					container.getPlayerIds().get(selectedIndex);
				container.updateUi(playerId);
			}
		});
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(abaloneGraphics);
		RootPanel.get("mainDiv").add(flowPanel);
		RootPanel.get("mainDiv").add(playerSelect);
		
		container.sendGameReady();
		container.updateUi(container.getPlayerIds().get(0));
	}
}