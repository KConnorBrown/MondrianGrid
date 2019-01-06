package grid;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.image.ImageView;

/*
 * key input handling class. abstracts out the user functionality from Grid
 * */
public class Control {

	private Grid _grid;
	private PaneOrganizer _po;
	private Pane _pane;
	private boolean _added;
	private ImageView _iv;

	public Control(Pane pane, Grid grid, PaneOrganizer po) {
		_grid = grid;
		_po = po;
		_pane = pane;
		_added = false;
		_pane.addEventHandler(KeyEvent.ANY, new KeyHandler());
	}

	private class KeyHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent e) {
			switch (e.getCode()) {

			case S:
				_grid.setMode(GridEnums.GO);
				break;

			case BACK_SPACE:
				_grid.setMode(GridEnums.STOP);
				_grid.reset();
				break;

			// displays the original colored image if shift and D are down
			case D:
				if (e.isShiftDown() && _po.getSource() != null) {
					if (!_added) {
						// add original image on top
						_iv = new ImageView();
						_iv.setImage(_po.getSource());
						_pane.getChildren().add(_iv);
						_added = true;
					}
				}
				break;

			case Z:
				// command z is undo
				if (e.isShortcutDown()) {
					_grid.undo();
				}
				break;

			case Y:
				// command y is redo
				if (e.isShortcutDown()) {
					_grid.redo();
				}
				break;

			// remove the displayed image if shift isn't down
			default:
				if (_iv != null && !e.isShiftDown()) {
					_pane.getChildren().remove(_iv);
					_added = false;
				}
				break;
			}
			e.consume();
		}
	}
}
