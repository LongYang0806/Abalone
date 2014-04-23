package org.abalone.graphics;

import org.abalone.client.AbaloneLogic;
import org.abalone.client.AbalonePresenter;
import org.game_api.GameApi;
import org.game_api.GameApi.Game;
import org.game_api.GameApi.IteratingPlayerContainer;
import org.game_api.GameApi.UpdateUI;
import org.game_api.GameApi.VerifyMove;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.theme.base.ButtonCss;
import com.googlecode.mgwt.ui.client.widget.Button;

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
		
//		final ListBox playerSelect = new ListBox();
//		playerSelect.addItem("White Player");
//		playerSelect.addItem("Red Player");
//		playerSelect.addItem("Viewer");
//		playerSelect.addChangeHandler(new ChangeHandler() {
//			@Override
//			public void onChange(ChangeEvent event) {
//				int selectedIndex = playerSelect.getSelectedIndex();
//				String playerId = selectedIndex == 2 ? GameApi.VIEWER_ID : 
//					container.getPlayerIds().get(selectedIndex);
//				container.updateUi(playerId);
//			}
//		});
		
		final HorizontalPanel buttonGroup = new HorizontalPanel();
		final ButtonCss buttonCss = MGWTStyle.getTheme().getMGWTClientBundle().getButtonCss();
    final Button redPlayer = new Button("White player");
    final Button blackPlayer = new Button("Red player");
    final Button viewer = new Button("Viewer"); 
    redPlayer.setSmall(true);
    blackPlayer.setSmall(true);
    viewer.setSmall(true);
    
    redPlayer.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        container.updateUi(container.getPlayerIds().get(0));
        redPlayer.addStyleName(buttonCss.active());
        blackPlayer.removeStyleName(buttonCss.active());
        viewer.removeStyleName(buttonCss.active());
      }                    
    });
    
    blackPlayer.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        container.updateUi(container.getPlayerIds().get(1));
        blackPlayer.addStyleName(buttonCss.active());
        redPlayer.removeStyleName(buttonCss.active());
        viewer.removeStyleName(buttonCss.active());
      }                    
    });
    
    viewer.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        container.updateUi(GameApi.VIEWER_ID);
        viewer.addStyleName(buttonCss.active());
        redPlayer.removeStyleName(buttonCss.active());
        blackPlayer.removeStyleName(buttonCss.active());
      }                    
    }); 
    
    buttonGroup.add(redPlayer);
    buttonGroup.add(blackPlayer);
    buttonGroup.add(viewer);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(abaloneGraphics);
		flowPanel.add(buttonGroup);
		RootPanel.get("mainDiv").add(flowPanel);
//		RootPanel.get("mainDiv").add(buttonGroup);
		
		container.sendGameReady();
		container.updateUi(container.getPlayerIds().get(0));
	}
}