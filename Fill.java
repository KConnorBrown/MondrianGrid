package grid;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

//model the act of filling a region. defines undo and redo for fill
public class Fill {

	private Color _prev;
	private Color _curr;
	private Rectangle _rect;

	public Fill(Color prev, Color curr, Rectangle region) {
		_prev = prev;
		_curr = curr;
		_rect = region;
	}

	public void undo() {
		_rect.setFill(_prev);
	}

	public void redo() {
		_rect.setFill(_curr);
	}

}
