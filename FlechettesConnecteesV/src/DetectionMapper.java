import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import coordinatesMapper.controller.IA;
import coordinatesMapper.model.Cible;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DetectionMapper {
	Map<String, DetectionLine> detectionLineDroit = new HashMap<>();
	Map<String, DetectionLine> detectionLineHaut = new HashMap<>();

	Set<Point2D> pointsDroit = new HashSet<>();
	Set<Point2D> pointsHaut = new HashSet<>();

	int minPointCommon = 5;

	int idCounterDroit = 0;
	int idCounterHaut = 0;

	DetectionImageDroit droitDetector;
	DetectionImageHaut hautDetector;

	Canvas canvas;

	List<Point2D> coordinates = new ArrayList<>();
	private Point2D vectorDroit;
	private Point2D vectorHaut;
	private Point2D cameraPositionDroit;
	private Point2D cameraPositionHaut;

	int currentScore = 0;
	List<Integer> scores = new ArrayList<>();

	protected DetectionMapper(DetectionImageDroit droitDetector, DetectionImageHaut hautDetector) {
		this.droitDetector = droitDetector;
		this.hautDetector = hautDetector;

		Stage stage = new Stage();
		Pane pane = new Pane();
		stage.setScene(new Scene(pane));

		canvas = new Canvas(1200, 1200);
		drawCanvasCoordinates();
		pane.getChildren().add(canvas);

		stage.setWidth(910);
		stage.setHeight(910);
		stage.show();
	}

	private void drawCanvasCoordinates() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

		double middleX = 300;
		double middleY = 300;

		double radiusCenter = 12.7;
		gc.strokeOval(middleX - radiusCenter / 2.0, middleY - radiusCenter / 2.0, radiusCenter, radiusCenter);

		double middleRadiusCenter = 31.8;
		gc.strokeOval(middleX - middleRadiusCenter / 2.0, middleY - middleRadiusCenter / 2.0, middleRadiusCenter,
				middleRadiusCenter);

		middleRadiusCenter = 107.4 * 2;
		gc.strokeOval(middleX - middleRadiusCenter / 2.0, middleY - middleRadiusCenter / 2.0, middleRadiusCenter,
				middleRadiusCenter);

		middleRadiusCenter = 170 * 2;
		gc.strokeOval(middleX - middleRadiusCenter / 2.0, middleY - middleRadiusCenter / 2.0, middleRadiusCenter,
				middleRadiusCenter);

		for (Point2D p : coordinates) {
			gc.strokeOval(middleX + (p.getX() * 10) - 2.5, middleY + (p.getY() * 10) - 2.5, 5, 5);
		}

		if (cameraPositionDroit != null && vectorDroit != null) {
			System.out.println(cameraPositionDroit);
			Point2D projection = cameraPositionDroit.add(vectorDroit.multiply(droitDetector.distanceCamera * 10));
			gc.strokeLine(middleX + cameraPositionDroit.getX() * 10, middleY + cameraPositionDroit.getY() * 10,
					middleX + projection.getX() * 10, middleY + projection.getY() * 10);
		}
		gc.setStroke(Color.RED);
		Point2D p = droitDetector.getCameraPosition();
		gc.strokeOval(middleX + (p.getX() * 10) - 2.5, middleY + (p.getY() * 10) - 2.5, 5, 5);
		// if (vectorDroit != null) {
		// gc.strokeOval(middleX + (vectorDroit.getX() * 10) - 2.5, middleY +
		// (vectorDroit.getY() * 10) - 2.5, 5, 5);
		//
		// }

		if (cameraPositionHaut != null && vectorHaut != null) {
			System.out.println(cameraPositionHaut);
			Point2D projection = cameraPositionHaut.add(vectorHaut.multiply(hautDetector.distanceCamera * 10));
			gc.strokeLine(middleX + cameraPositionHaut.getX() * 10, middleY + cameraPositionHaut.getY() * 10,
					middleX + projection.getX() * 10, middleY + projection.getY() * 10);
		}
		gc.setStroke(Color.GREEN);
		p = hautDetector.getCameraPosition();
		gc.strokeOval(middleX + (p.getX() * 10) - 2.5, middleY + (p.getY() * 10) - 2.5, 5, 5);
		// if (vectorHaut != null) {
		// gc.strokeOval(middleX + (vectorHaut.getX() * 10) - 2.5, middleY +
		// (vectorHaut.getY() * 10) - 2.5, 5, 5);
		//
		// }
	}

	public void addLineDectionHaut(DetectionLine line) {
		int commonConter = 0;
		for (Point2D p : line.getPoints()) {
			if (pointsDroit.contains(new Point2D((int) p.getX(), (int) p.getY()))) {
				commonConter++;
			}
			if (commonConter > minPointCommon) {
				return;
			}
		}
		for (Point2D p : line.getPoints()) {
			pointsDroit.add(new Point2D((int) p.getX(), (int) p.getY()));
		}

		line.setId(Integer.toString(++idCounterHaut));
		System.out.println("Add Haut " + line.getId());
		detectionLineHaut.put(line.getId(), line);
		onLineAdded(line.getId());
	}

	protected void addLineDectionDroit(DetectionLine line) {
		int commonConter = 0;
		for (Point2D p : line.getPoints()) {
			if (pointsHaut.contains(new Point2D((int) p.getX(), (int) p.getY()))) {
				commonConter++;
			}
			if (commonConter > minPointCommon) {
				return;
			}
		}
		for (Point2D p : line.getPoints()) {
			pointsHaut.add(new Point2D((int) p.getX(), (int) p.getY()));
		}
		line.setId(Integer.toString(++idCounterDroit));
		System.out.println("Add Droit " + line.getId());
		detectionLineDroit.put(line.getId(), line);
		onLineAdded(line.getId());
	}

	private void onLineAdded(String id) {
		DetectionLine lineDroit = detectionLineDroit.get(id);
		DetectionLine lineHaut = detectionLineHaut.get(id);
		if (lineDroit != null && lineHaut != null) {
			Point2D droitIntersectionPoint = droitDetector.getInteractionPoint(lineDroit.getPoints())
					.subtract(droitDetector.imageWidth / 2.0, droitDetector.imageHeight / 2.0);
			Point2D hautIntersectionPoint = hautDetector.getInteractionPoint(lineDroit.getPoints())
					.subtract(droitDetector.imageWidth / 2.0, droitDetector.imageHeight / 2.0);

			vectorDroit = droitDetector.getProjection(droitIntersectionPoint.getX());
			vectorHaut = hautDetector.getProjection(hautIntersectionPoint.getX());

			cameraPositionDroit = droitDetector.getCameraPosition();
			cameraPositionHaut = hautDetector.getCameraPosition();

			Vector3D apacheVectorDroit = new Vector3D(vectorDroit.getX(), vectorDroit.getY(), 0);
			Vector3D apacheVectorHaut = new Vector3D(vectorHaut.getX(), vectorHaut.getY(), 0);

			Vector3D apacheCameraDroit = new Vector3D(cameraPositionDroit.getX(), cameraPositionDroit.getY(), 0);
			Vector3D apacheCameraHaut = new Vector3D(cameraPositionHaut.getX(), cameraPositionHaut.getY(), 0);

			Line lineDroit2D = new Line(apacheCameraDroit, apacheCameraDroit.add(apacheVectorDroit), 0.1);
			Line lineHaut2D = new Line(apacheCameraHaut, apacheCameraHaut.add(apacheVectorHaut), 0.1);

			Vector3D v = lineDroit2D.intersection(lineHaut2D);
			Point2D p = new Point2D(v.getX(), v.getY());
			System.out.println(p);
			coordinates.add(p);

			double[] rayons = { 0.635, 1.59, 10.74 - 0.8, 10.74, 17 - 0.8, 17 };
			IA sonny = new IA();
			Cible cible = sonny.cibleBuilder(rayons);
			int nbPoint = sonny.getPoints(p.getX(), p.getY(), cible);
			scores.add(nbPoint);
			currentScore += nbPoint;
			drawCanvasCoordinates();

			reinitializeScore();
		}
	}

	public void reinitializeScore() {
		currentScore = 0;
		scores = new ArrayList<>();
	}

	public int getCurrentScore() {
		return currentScore;
	}

	public List<Integer> getScores() {
		return scores;
	}

}
