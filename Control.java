package grid;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;

/*
 * key input handling class. abstracts out the user functionality from Grid
 * */
public class Control {

	private Grid _grid;
	private PaneOrganizer _po;
	private Pane _pane;
	private boolean _added;
	private ImageView _iv;
	private Stage _stage;
	private FileChooser _fc;
	private boolean _helping;
	private HelpBox _hb;
	private int _oCount;
	private int _sCount;

	public Control(Pane pane, Grid grid, PaneOrganizer po, Stage stage) {
		_grid = grid;
		_po = po;
		_pane = pane;
		_added = false;
		_helping = true;
		_oCount = 0;
		_sCount = 0;
		_stage = stage;
		_pane.addEventHandler(KeyEvent.ANY, new KeyHandler());
		_hb = _grid.getHB();
	}

	private class KeyHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent e) {
			switch (e.getCode()) {

			// the user presses the enter key to start the program
			case ENTER:
				_grid.setMode(GridEnums.GO);
				if (_helping && _grid.isClear()) {
					_hb.remove();
					_helping = false;
				}				
				break;

			/*
			 * backspace clears the canvas if there are any grids on it or any colors or
			 * alterations to the canvas or background image
			 */
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
						_iv.fitWidthProperty().bind(_stage.widthProperty());
						_iv.setImage(_po.getSource());
						_iv.setPreserveRatio(true);
						_pane.getChildren().add(_iv);
						_added = true;
					}
				}
				break;

			// command o opens the file chooser to choose a file to load to the canvas
			case O:
				if (e.isShortcutDown() && _oCount % 2 == 0) {
					_oCount++;
					_fc = new FileChooser();
					_fc.setTitle("Open Resource File");
					File file = _fc.showOpenDialog(_stage);
					if (file != null) {
						// clear the grid
						// load a copy of the image into the canvas to work with
						_grid.reset();
						_po.setSource(new Image(file.toURI().toString()));
					}
				} else {
					_oCount = 0;
				}
				break;

			// command s opens the file chooser to save the image that's currently in the
			// canvas
			case S:
				if (e.isShortcutDown() && _sCount % 2 == 0) {
					_sCount++;
					_fc = new FileChooser();
					_fc.setTitle("Saving scene snapshot");
					_fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
					File file = _fc.showSaveDialog(null);
					if (file != null) {
						try {
							WritableImage writableImage = new WritableImage((int) Constants.ROOT_WIDTH,
									(int) Constants.ROOT_HEIGHT);
							_pane.snapshot(null, writableImage);
							RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
							// Write the snapshot to the chosen file
							ImageIO.write(renderedImage, "png", file);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				} else {
					_sCount = 0;
				}
				break;

			// shift g set the background image to gray scale
			case G:
				if (e.isShiftDown()) {
					ColorAdjust colorAdjust = new ColorAdjust();
					colorAdjust.setSaturation(-1);
					colorAdjust.setContrast(0.05);
					colorAdjust.setHue(-0.05);
					colorAdjust.setBrightness(-0.1);
					_po.getImageView().setEffect(colorAdjust);
				}
				break;

			/* R B and Y set the fill color to red blue or yellow respectively */
			case R:
				_grid.setColor(Color.RED);
				break;
			case B:
				_grid.setColor(Color.BLUE);
				break;
			case Y:
				_grid.setColor(Color.YELLOW);
				break;

			// typing H displays the user keyboard controls
			case H:
				if (!_helping) {
					_helping = true;
					_hb = new HelpBox(_pane);
				}
				break;
			case ESCAPE:
				if (_helping) {
					_hb.remove();
					_helping = false;
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
