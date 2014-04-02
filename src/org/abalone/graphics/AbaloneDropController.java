package org.abalone.graphics;

import static org.abalone.client.AbaloneConstants.picWidth;
import static org.abalone.client.AbaloneConstants.picHight;

import org.abalone.client.AbalonePresenter;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.Image;

public class AbaloneDropController extends SimpleDropController{
	
	private final Image image;
	private final AbalonePresenter presenter;
	
	public AbaloneDropController(Image image, AbalonePresenter presenter) {
		super(image);
		this.image = image;
		this.presenter = presenter;
	}
	
	@Override
	public void onDrop(DragContext context) {
		System.out.println("On Drop");
		int x = image.getAbsoluteTop() / picWidth;
		int y = image.getAbsoluteLeft() / picHight;
		presenter.placedOnePiece(x, y);
	}
	
	@Override
	public void onPreviewDrop(DragContext context) 
			throws VetoDragException {
		if(image == null) {
			throw new VetoDragException();
		}
		super.onPreviewDrop(context);
	}
}
