package org.abalone.graphics;

import static org.abalone.client.AbaloneConstants.picWidth;
import static org.abalone.client.AbaloneConstants.picHight;

import org.abalone.client.AbalonePresenter;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class AbaloneDragController extends PickupDragController {
	
	private final AbalonePresenter presenter;
	
	public AbaloneDragController(AbsolutePanel board, boolean allowDroppingOnBoundaryPanel,
			AbalonePresenter presenter) {
		super(board, allowDroppingOnBoundaryPanel);
		this.presenter = presenter;
	}
	
	@Override
	public void dragStart() {
		System.out.println("Drag Start");
		super.dragStart();
		saveSelectedWidgetsLocationAndStyle();
		Image image = (Image) context.draggable;
		int x = image.getAbsoluteTop() / picHight;
		int y = image.getAbsoluteLeft() / picWidth;
		presenter.heldOnePiece(x, y);
	}
}
