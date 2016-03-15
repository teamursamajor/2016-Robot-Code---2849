package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	private ImageView webcamViewer = new ImageView();
	
	private PictureClient picClient;

	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = FXMLLoader.load(getClass().getResource("DashboardFXML.fxml"));
			root.getChildren().addAll(webcamViewer);
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		webcamViewer.setCache(true);
		webcamViewer.setFitWidth(640);
		webcamViewer.setPreserveRatio(true);
		picClient = new PictureClient(webcamViewer, 5805);
		Thread pictureThread = new Thread(picClient);
		pictureThread.start();
	}
	
	@Override
	public void stop() {
		picClient.stop();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
