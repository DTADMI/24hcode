import javafx.geometry.Point2D;

public class DetectionImageHaut extends DetectionImage {

	public DetectionImageHaut() {
		super();
		sigma = 4.83;
		lowerThreshold = 0.51;
		upperThreshold = 2.55;
		minLineLength = 130;
		maxLineLength = 0;
		roiXPosition = 276;
		roiYPosition = 212;
		roiWidth = 749;
		roiHeight = 223;
		distanceCamera = 44.5;
		factorCamera = 16.50;
		middleTargetImageX = 643;
		middleTargetImageY = 393;
	}

	@Override
	public Point2D getProjection(double position) {
		Point2D cameraPosition = getCameraPosition();
		double offsetCenter = getOffsetCameraToTargetCenterReal().getX();
		double offset = position / factorCamera;
		Point2D centerImageOnTarget = new Point2D(offsetCenter + offset, 0);
		return centerImageOnTarget.subtract(cameraPosition).normalize();

	}

	@Override
	public Point2D getCameraPosition() {
		double offsetCenter = getOffsetCameraToTargetCenterReal().getX();
		return new Point2D(offsetCenter, distanceCamera);
	}
}
