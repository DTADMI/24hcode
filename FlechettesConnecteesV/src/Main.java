import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

	private ImageView imageViewDroit;
	private ImageView imageViewHaut;

	private Canvas canvasDroit;
	private Canvas canvasHaut;

	private DetectionImageDroit detectionDroit;
	private DetectionImageHaut detectionHaut;

	private GraphicsContext gcDroit;
	private GraphicsContext gcHaut;

	private DetectionMapper mapper;

	public static void main(String[] args) {
		launch(args);
	}

	Runnable getDroitRunnable() {
		return new Runnable() {

			@Override
			public void run() {
				File[] files = new File("C:\\Users\\k008196\\Downloads\\testimagesDroit").listFiles();
				detectionDroit.setRef(null);
				for (File f : files) {
					try {
						BufferedImage image = ImageIO.read(f);
						if (detectionDroit.getRef() == null) {
							detectionDroit.setRef(image);
						} else {
							final List<DetectionLine> points2 = detectionDroit.getPoints(image);
							if (points2 != null)
								for (DetectionLine dl : points2) {
									mapper.addLineDectionDroit(dl);
								}
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									imageViewDroit.setImage(SwingFXUtils.toFXImage(image, null));
								}
							});
							if (points2 != null) {
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										for (DetectionLine points : points2) {
											Point2D previousPoint = null;
											for (Point2D p : points.getPoints()) {
												if (previousPoint != null) {
													gcDroit.strokeLine(previousPoint.getX(), previousPoint.getY(),
															p.getX(), p.getY());
												}
												previousPoint = p;
											}
										}
									}
								});

							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		};
	}

	Runnable getHautRunnable() {
		return new Runnable() {

			@Override
			public void run() {
				File[] files = new File("C:\\Users\\k008196\\Downloads\\testimagesHaut").listFiles();
				detectionHaut.setRef(null);
				for (File f : files) {
					try {
						BufferedImage image = ImageIO.read(f);
						if (detectionHaut.getRef() == null) {
							detectionHaut.setRef(image);
						} else {
							final List<DetectionLine> points2 = detectionHaut.getPoints(image);
							if (points2 != null)
								for (DetectionLine dl : points2) {
									mapper.addLineDectionHaut(dl);
								}
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									imageViewHaut.setImage(SwingFXUtils.toFXImage(image, null));
								}
							});
							if (points2 != null) {
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										for (DetectionLine points : points2) {
											Point2D previousPoint = null;
											for (Point2D p : points.getPoints()) {
												if (previousPoint != null) {
													gcHaut.strokeLine(previousPoint.getX(), previousPoint.getY(),
															p.getX(), p.getY());
												}
												previousPoint = p;
											}
										}
									}
								});

							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		};
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setWidth(1000);
		primaryStage.setHeight(1000);

		ScrollPane scrollPane = new ScrollPane();
		VBox box = new VBox();
		scrollPane.setContent(box);

		Scene scene = new Scene(scrollPane);
		primaryStage.setScene(scene);
		primaryStage.show();

		detectionDroit = new DetectionImageDroit();
		StackPane stackPaneDroit = new StackPane();
		box.getChildren().add(stackPaneDroit);
		imageViewDroit = new ImageView();
		stackPaneDroit.getChildren().add(imageViewDroit);

		canvasDroit = new Canvas(detectionDroit.imageWidth, detectionDroit.imageHeight);
		gcDroit = canvasDroit.getGraphicsContext2D();
		gcDroit.setStroke(Color.HOTPINK);
		stackPaneDroit.getChildren().add(canvasDroit);

		detectionHaut = new DetectionImageHaut();
		StackPane stackPaneHaut = new StackPane();
		box.getChildren().add(stackPaneHaut);
		imageViewHaut = new ImageView();
		stackPaneHaut.getChildren().add(imageViewHaut);

		canvasHaut = new Canvas(detectionHaut.imageWidth, detectionHaut.imageHeight);
		gcHaut = canvasHaut.getGraphicsContext2D();
		gcHaut.setStroke(Color.HOTPINK);
		stackPaneHaut.getChildren().add(canvasHaut);

		mapper = new DetectionMapper(detectionDroit, detectionHaut);

		Thread threadDroit = new Thread(getDroitRunnable());
		threadDroit.start();

		Thread threadHaut = new Thread(getHautRunnable());
		threadHaut.start();
	}
}
