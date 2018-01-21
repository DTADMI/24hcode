import java.io.FileInputStream;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main2 extends Application {

	private static final int SIZE_TARGET = 17;
	DetectionImageDroit imageDroit = new DetectionImageDroit();
	private GraphicsContext gc;
	private Canvas canvas;
	private Point2D mousePosition = null;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setWidth(1300);
		primaryStage.setHeight(750);

		Pane pane = new Pane();

		ImageView image = new ImageView(
				new Image(new FileInputStream("C:\\Users\\k008196\\Downloads\\Img\\CAM-DROITE-JPG11.jpg")));
		image.setFitHeight(720);
		image.setFitWidth(1280);
		image.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				mousePosition = new Point2D(event.getSceneX(), event.getSceneY());
				draw();
			}
		});
		pane.getChildren().add(image);

		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.show();

		displayScheme();

	}

	public void displayScheme() {
		Stage stage = new Stage();
		stage.setWidth(660);
		stage.setHeight(250);
		Pane pane = new Pane();
		stage.setScene(new Scene(pane));
		stage.show();

		canvas = new Canvas(SIZE_TARGET * 30, SIZE_TARGET * 30);
		pane.getChildren().add(canvas);
		draw();

	}

	private void draw() {
		double ratio = 10;
		gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

		gc.strokeOval(-17 * ratio, (gc.getCanvas().getHeight() / 2.0) - (17 * ratio), 17 * 2 * ratio, 17 * 2 * ratio);
		// gc.strokeOval(0, 0, canvas.getHeight(), canvas.getHeight());
		Point2D cameraPosition = new Point2D((imageDroit.distanceCamera * ratio), gc.getCanvas().getHeight() / 2.0)
				.subtract(0, imageDroit.getOffsetCameraToTargetCenterReal().getX() * ratio);
		// gc.strokeOval(cameraPosition.getX(), cameraPosition.getY(), 5, 5);

		Point2D projectedPositionMin = cameraPosition
				.add(imageDroit.getProjection(0).multiply(imageDroit.distanceCamera * ratio));
		gc.strokeLine(cameraPosition.getX(), cameraPosition.getY(), projectedPositionMin.getX(),
				projectedPositionMin.getY());

		Point2D projectedPositionMax = cameraPosition
				.add(imageDroit.getProjection(1280).multiply(imageDroit.distanceCamera * ratio));
		gc.strokeLine(cameraPosition.getX(), cameraPosition.getY(), projectedPositionMax.getX(),
				projectedPositionMax.getY());

		if (mousePosition != null) {
			Point2D projectedPosition = cameraPosition
					.add(imageDroit.getProjection(mousePosition.getX()).multiply(imageDroit.distanceCamera * ratio));
			// System.out.println(cameraPosition + " " + projectedPosition);
			gc.strokeLine(cameraPosition.getX(), cameraPosition.getY(), projectedPosition.getX(),
					projectedPosition.getY());
		}
	}

}
