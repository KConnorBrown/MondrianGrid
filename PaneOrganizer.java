package grid;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.effect.ColorAdjust;
/*
 * This is my PaneOrganizer class. It factors out the graphical layout from the App class and deals
 * with organizing all the graphical elements within the root (border pane).
 */
public class PaneOrganizer {

	private BorderPane _root;
	private Image _source;

	/*
	 * My PaneOrganizer constructor takes in a stage in its constructor to pass on
	 * to an instance of Control which uses it in the save and load handling. In my
	 * constructor I instantiate control and sketchy and make the proper
	 * associations between them.
	 */
	public PaneOrganizer(Stage stage) {
		_root = new BorderPane();
		_root.setPrefSize(Constants.ROOT_WIDTH, Constants.ROOT_HEIGHT);
		_root.setStyle("-fx-background-color: white;");
		Pane canvasPane = new Pane();
		this.installImage(canvasPane);
		canvasPane.setFocusTraversable(true);
		Grid grid = new Grid(canvasPane);
		new Control(canvasPane, grid, this);
	}

	// returns the BorderPane instance
	public BorderPane getRoot() {
		return _root;
	}
	
	//returns color image
	public Image getSource() {
		return _source;
	}
	
	
	//installs grayscale background image of MK and A Olsen
	private void installImage(Pane canvasPane) {
		ColorAdjust colorAdjust = new ColorAdjust();
		colorAdjust.setSaturation(-1);
		colorAdjust.setContrast(0.05);
		colorAdjust.setHue(-0.05);
		colorAdjust.setBrightness(-0.1);
		_source = new Image("https://ewedit.files.wordpress.com/2015/04/billboard-dad.jpg", Constants.ROOT_WIDTH, Constants.ROOT_HEIGHT, true, true);
		canvasPane.setPrefSize(Constants.ROOT_WIDTH, Constants.ROOT_HEIGHT);
		WritableImage image = new WritableImage(_source.getPixelReader(), (int)_source.getWidth(), (int)_source.getHeight());
		ImageView iv = new ImageView();
		iv.setImage(image);
		iv.setFitWidth(Constants.ROOT_WIDTH);
		iv.setPreserveRatio(true);
		iv.setEffect(colorAdjust);
		_root.setCenter(canvasPane);
		canvasPane.getChildren().add(iv);
	}

}