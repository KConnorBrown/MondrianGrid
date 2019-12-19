package grid;


import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class PaneOrganizer {

	private BorderPane _root;
	private Image _source;
	private ImageView _iv;
	private Pane _canvasPane;
	private Stage _stage;
	private double _rootHeight;

	public PaneOrganizer(Stage stage) {
		_stage = stage;
		_root = new BorderPane();
		_root.setPrefSize(Constants.ROOT_WIDTH, Constants.ROOT_HEIGHT);
		_rootHeight = Constants.ROOT_HEIGHT;
		_root.setStyle("-fx-background-color: white;");
		_canvasPane = new Pane();
		_canvasPane.setPrefSize(Constants.ROOT_WIDTH, Constants.ROOT_HEIGHT);
		this.setSource(null);
		_canvasPane.setFocusTraversable(true);
		Grid grid = new Grid(_canvasPane, this);
		new Control(_canvasPane, grid, this, stage);
	}

	public BorderPane getRoot() {
		return _root;
	}

	// returns color image
	public Image getSource() {
		return _source;
	}

	public ImageView getImageView() {
		return _iv;
	}

	// updates the source image displayed
	public void setSource(Image image) {
		if (_iv == null) {
			_iv = new ImageView();
			_iv.fitWidthProperty().bind(_stage.widthProperty());
			_iv.fitHeightProperty().bind(_stage.heightProperty());
			_root.setCenter(_canvasPane);
			_canvasPane.getChildren().add(_iv);
		}
		if (image == null) {
			return;
		}
		WritableImage img = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
		_iv.setImage(img);
		_iv.setPreserveRatio(true);
		_source = img;
		

/*
commented out code represents unfinished attempts at image resizing
*/
		//if portrait style
//		double htwRatio = img.getHeight()/img.getWidth();
//		//if portrait
//		if (htwRatio > 1) {
//			
//		}
//		if (_source.getHeight() > _source.getWidth()) {
//			//remove the empty space
//		}
//		if (_source.getHeight() < Constants.ROOT_HEIGHT) {
//			System.out.println("image too short");
//			_stage.setWidth(Constants.ROOT_WIDTH);
//			_stage.setHeight(_source.getHeight());
//			_rootHeight = _source.getHeight();
//		}
	}

	public double getRootHeight() {
		return _rootHeight;
	}

}
