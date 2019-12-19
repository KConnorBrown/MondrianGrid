package grid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * This is my App class. It deals with the highest level of graphics. It passes
 * an instance of Stage to the PaneOrganizer constructor to be used in my loading
 * and saving handlers.
 *
 */

public class App extends Application {

	@Override
	public void start(Stage stage) {
		PaneOrganizer organizer = new PaneOrganizer(stage);
		Scene scene = new Scene(organizer.getRoot());
		stage.setScene(scene);
		stage.setTitle("Mondrian Visualizer");
		stage.show();
	}

	/*
	 * Here is the mainline! No need to change this.
	 */
	public static void main(String[] argv) {
		// launch is a method inherited from Application
		launch(argv);
	}
}

