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
		factorCamera = 18.0;
		middleTargetImageX = 758;
		middleTargetImageY = 393;
	}

	@Override
	public Point2D getProjection(double position) {
		double centerScreen = imageWidth / 2.0;
		double positionRelativeToScreen = position - centerScreen;
		double positionRelativeToScreenCm = positionRelativeToScreen / getHorizontalFactorCamera();

		double deltaXWithTarget = positionRelativeToScreenCm;

		double offsetCenter = getOffsetCameraToTargetCenterReal().getY();
		Point2D cameraPosition = getCameraPosition();
		Point2D targetPositionEstimated = new Point2D(offsetCenter + deltaXWithTarget, 0);
		return targetPositionEstimated.subtract(cameraPosition).normalize();

	}

	@Override
	public Point2D getCameraPosition() {
		double offsetCenter = getOffsetCameraToTargetCenterReal().getY();
		return new Point2D(offsetCenter, distanceCamera);
	}
}
