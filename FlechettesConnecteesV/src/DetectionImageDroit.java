import javafx.geometry.Point2D;

public class DetectionImageDroit extends DetectionImage {

	public DetectionImageDroit() {
		super();
		sigma = 4.83;
		lowerThreshold = 0.51;
		upperThreshold = 2.55;
		minLineLength = 130;
		maxLineLength = 0;
		roiXPosition = 318;
		roiYPosition = 246;
		roiHeight = 196;
		roiWidth = 859;
		distanceCamera = 38.5;
		factorCamera = 18.5;
		middleTargetImageX = 758;
		middleTargetImageY = 395;
		System.out.println(getCameraPosition());
		System.out.println();
	}

	@Override
	public Point2D getProjection(double position) {
		Point2D cameraPosition = getCameraPosition();
		double offsetCenter = getOffsetCameraToTargetCenterReal().getX();
		double offset = position / factorCamera;
		Point2D centerImageOnTarget = new Point2D(0, offsetCenter + offset);
		return centerImageOnTarget.subtract(cameraPosition).normalize();

	}

	@Override
	public Point2D getCameraPosition() {
		double offsetCenter = getOffsetCameraToTargetCenterReal().getX();
		return new Point2D(distanceCamera, offsetCenter);
	}
}
