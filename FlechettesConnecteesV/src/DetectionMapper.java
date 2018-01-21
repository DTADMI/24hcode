import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import javafx.geometry.Point2D;

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

	protected DetectionMapper(DetectionImageDroit droitDetector, DetectionImageHaut hautDetector) {
		this.droitDetector = droitDetector;
		this.hautDetector = hautDetector;
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
			System.out.println("Line Added = " + id);
			Point2D droitIntersectionPoint = droitDetector.getInteractionPoint(lineDroit.getPoints());
			Point2D hautIntersectionPoint = hautDetector.getInteractionPoint(lineDroit.getPoints());

			Point2D vectorDroit = droitDetector.getProjection(droitIntersectionPoint.getX());
			Point2D vectorHaut = hautDetector.getProjection(hautIntersectionPoint.getY());

			Point2D cameraPositionDroit = droitDetector.getCameraPosition();
			Point2D cameraPositionHaut = hautDetector.getCameraPosition();

			Vector3D apacheVectorDroit = new Vector3D(vectorDroit.getX(), vectorDroit.getY(), 0);
			Vector3D apacheVectorHaut = new Vector3D(vectorHaut.getX(), vectorHaut.getY(), 0);

			Vector3D apacheCameraDroit = new Vector3D(cameraPositionDroit.getX(), cameraPositionDroit.getY(), 0);
			Vector3D apacheCameraHaut = new Vector3D(cameraPositionHaut.getX(), cameraPositionHaut.getY(), 0);

			Line lineDroit2D = new Line(apacheCameraDroit, apacheCameraDroit.add(apacheVectorDroit), 0.1);
			Line lineHaut2D = new Line(apacheCameraHaut, apacheCameraHaut.add(apacheVectorHaut), 0.1);

			System.out.println(lineDroit2D.intersection(lineHaut2D));
		}
	}

}
