import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntFunction;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import de.biomedical_imaging.ij.steger.Line;
import de.biomedical_imaging.ij.steger.LineDetector;
import de.biomedical_imaging.ij.steger.Lines;
import ij.ImagePlus;
import ij.Prefs;
import ij.Undo;
import ij.plugin.ImageCalculator;
import ij.plugin.Thresholder;
import ij.plugin.filter.Binary;
import ij.plugin.filter.Filters;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import javafx.geometry.Point2D;

public abstract class DetectionImage {

	double sigma = 0;
	double lowerThreshold = 0;
	double upperThreshold = 0;
	int minLineLength = 0;
	int maxLineLength = 0;
	int roiXPosition = 0;
	int roiYPosition = 0;
	int roiHeight = 0;
	int roiWidth = 0;
	int imageWidth = 1280;
	int imageHeight = 720;
	double distanceCamera = 38.5;
	double factorCamera = 0.0;
	int middleTargetImageX = 0;
	int middleTargetImageY = 0;

	ImagePlus ref;
	ImagePlus currentImg;

	public DetectionImage() {

	}

	public Point2D getOffsetCameraToTargetCenterReal() {
		Point2D centerTarget = new Point2D(middleTargetImageX, middleTargetImageY);
		Point2D centerImage = new Point2D(imageWidth / 2.0, imageHeight / 2.0);
		Point2D delta = centerTarget.subtract(centerImage);
		return new Point2D(delta.getX() / getHorizontalFactorCamera(), delta.getY() / getVerticalFactorCamera());
	}

	public void setRef(BufferedImage image) {
		if (image == null) {
			return;
		}
		ref = new ImagePlus("", image);
		ref = getROI(ref, roiXPosition, roiYPosition, roiWidth, roiHeight);
		ref = filterWithFindEdges(ref);
	}

	public static ImagePlus filterWith8bits(ImagePlus image) {
		ImageConverter converter = new ImageConverter(image);
		converter.convertToGray8();
		return image;
	}

	public static ImagePlus getROI(ImagePlus image, int x, int y, int width, int height) {
		ImageProcessor ip = image.getProcessor();
		ip.setRoi(x, y, width, height);
		ImageProcessor cropped = ip.crop();
		return new ImagePlus("", cropped);
	}

	public static ImagePlus filterWithReference(ImagePlus image, ImagePlus reference) {
		ImageCalculator imageCalculator = new ImageCalculator();
		imageCalculator.run("difference", image, reference);
		return image;
	}

	public static ImagePlus filterWithFindEdges(ImagePlus image) {
		Filters filter = new Filters();
		filter.setup("edge", image);
		filter.run(image.getProcessor());
		return image;
	}

	public static ImagePlus filterWithBinary(ImagePlus image) {
		Prefs.blackBackground = false;
		Thresholder treshold = new Thresholder();
		image.setActivated();
		Undo.setup(Undo.TRANSFORM, image);
		Method method;
		try {
			method = Thresholder.class.getDeclaredMethod("applyThreshold", ImagePlus.class, boolean.class);
			method.setAccessible(true);
			method.invoke(treshold, image, false);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return image;
	}

	public static ImagePlus filterWithFillHoles(ImagePlus image) {
		Binary binary = new Binary();
		binary.setup("fill", image);
		binary.run(image.getProcessor());
		return image;
	}

	public List<DetectionLine> getSegments(ImagePlus image) {
		LineDetector lineDetector = new LineDetector();
		Lines lines = lineDetector.detectLines(image.getProcessor(), sigma, upperThreshold, lowerThreshold,
				minLineLength, maxLineLength, false, true, false, true);
		if (lines.isEmpty()) {
			return null;
		}
		List<DetectionLine> points2 = new ArrayList<>();
		for (Line l : lines) {
			List<Point2D> points = new ArrayList<>();
			for (int i = 0; i < l.getNumber(); i++) {
				points.add(new Point2D((double) l.getXCoordinates()[i] + roiXPosition,
						(double) l.getYCoordinates()[i] + roiYPosition));
			}
			Point2D firstPoint = points.get(0);
			Point2D lastPoint = points.get(points.size() - 1);
			double diffX = Math.abs(lastPoint.getX() - firstPoint.getX());
			double diffY = Math.abs(lastPoint.getY() - firstPoint.getY());
			if (diffY > diffX) {
				DetectionLine line = new DetectionLine();
				line.getPoints().addAll(points);
				points2.add(line);
			}
		}
		Collections.sort(points2, new Comparator<DetectionLine>() {

			@Override
			public int compare(DetectionLine o1, DetectionLine o2) {
				return Double.compare(o1.getPoints().get(0).getX(), o2.getPoints().get(0).getX());
			}
		});
		int id = 1;
		for (DetectionLine dl : points2) {
			dl.setId(Integer.toString(id));
			id++;
		}
		return points2;
	}

	public List<DetectionLine> getPoints(BufferedImage currentImage) {
		ImagePlus img = new ImagePlus("", currentImage);
		img = getROI(img, roiXPosition, roiYPosition, roiWidth, roiHeight);
		img = filterWithFindEdges(img);
		img = filterWithReference(img, ref);
		img = filterWith8bits(img);
		img = filterWithBinary(img);
		img = filterWithFillHoles(img);

		currentImg = img;
		List<DetectionLine> points = getSegments(img);
		if (points == null) {
			return null;
		}
		// System.out.println(points.size());
		// ref = img;
		return points;
	}

	public double getAngleCameraHorizontal() {
		double cameraWidthReal = (imageWidth / 2.0) / factorCamera;
		return Math.atan(cameraWidthReal / distanceCamera);
	}

	public double getAngleCameraVertical() {
		return ((double) imageWidth / (double) imageHeight) * getAngleCameraHorizontal();
	}

	public double getHorizontalFactorCamera() {
		return factorCamera;
	}

	public double getVerticalFactorCamera() {
		return Math.abs(distanceCamera * Math.tan(getAngleCameraVertical()));
	}

	public abstract Point2D getProjection(double position);

	public ImagePlus getRef() {
		return ref;
	}

	public ImagePlus getCurrentImg() {
		return currentImg;
	}

	public Point2D getInteractionPoint(List<Point2D> points) {
		Point2D lowestPoint = null;
		for (Point2D p : points) {
			if (lowestPoint == null || lowestPoint.getY() > p.getY()) {
				lowestPoint = p;
			}
		}
		return lowestPoint;
	}

	public Point2D getCameraPosition() {
		// TODO Auto-generated method stub
		return null;
	}

}
