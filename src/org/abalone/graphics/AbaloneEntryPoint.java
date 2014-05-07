package org.abalone.graphics;

import org.abalone.client.AbaloneLogic;
import org.abalone.client.AbalonePresenter;
import org.abalone.i18n.AbaloneMessages;
import org.game_api.GameApi.ContainerConnector;
import org.game_api.GameApi.Game;
import org.game_api.GameApi.UpdateUI;
import org.game_api.GameApi.VerifyMove;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AbaloneEntryPoint implements EntryPoint {
//	IteratingPlayerContainer container;
 	ContainerConnector container;
	AbalonePresenter abalonePresenter;
	
	private AbaloneMessages abaloneMessages = GWT.create(AbaloneMessages.class);

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
//		container = new IteratingPlayerContainer(game, 2);
		container = new ContainerConnector(game);
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
		
//		final HorizontalPanel buttonGroup = new HorizontalPanel();
//		final ButtonCss buttonCss = MGWTStyle.getTheme().getMGWTClientBundle().getButtonCss();
//    final Button whitePlayer = new Button(abaloneMessages.whitePlayer());
//    final Button redPlayer = new Button(abaloneMessages.redPlayer());
//    final Button viewer = new Button(abaloneMessages.viewer()); 
//    whitePlayer.setSmall(true);
//    redPlayer.setSmall(true);
//    viewer.setSmall(true);
//    
//    // white player plays first
//    whitePlayer.addStyleName(buttonCss.active());
//    
//    whitePlayer.addTapHandler(new TapHandler() {
//      @Override
//      public void onTap(TapEvent event) {
//        container.updateUi(container.getPlayerIds().get(0));
//        whitePlayer.addStyleName(buttonCss.active());
//        redPlayer.removeStyleName(buttonCss.active());
//        viewer.removeStyleName(buttonCss.active());
//      }                    
//    });
//    
//    redPlayer.addTapHandler(new TapHandler() {
//      @Override
//      public void onTap(TapEvent event) {
//        container.updateUi(container.getPlayerIds().get(1));
//        redPlayer.addStyleName(buttonCss.active());
//        whitePlayer.removeStyleName(buttonCss.active());
//        viewer.removeStyleName(buttonCss.active());
//      }                    
//    });
//    
//    viewer.addTapHandler(new TapHandler() {
//      @Override
//      public void onTap(TapEvent event) {
//        container.updateUi(GameApi.VIEWER_ID);
//        viewer.addStyleName(buttonCss.active());
//        whitePlayer.removeStyleName(buttonCss.active());
//        redPlayer.removeStyleName(buttonCss.active());
//      }                    
//    }); 
//    
//    buttonGroup.add(whitePlayer);
//    buttonGroup.add(redPlayer);
//    buttonGroup.add(viewer);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(abaloneGraphics);
		console(abaloneGraphics.getOffsetHeight() + " width " + abaloneGraphics.getOffsetWidth());
//		flowPanel.add(buttonGroup);
		RootPanel.get("mainDiv").add(flowPanel);
//		RootPanel.get("mainDiv").add(buttonGroup);
		
		container.sendGameReady();
//		container.updateUi(container.getPlayerIds().get(0));
	}
	
	public static native void console(String text)
	/*-{
	   console.log(text);
	}-*/;
}