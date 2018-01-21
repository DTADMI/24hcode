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
		distanceCamera = 44.5;
		factorCamera = 15.6;
		middleTargetImageX = 758;
		middleTargetImageY = 395;
	}

	@Override
	public Point2D getProjection(double position) {
		double centerScreen = imageWidth / 2.0;
		double positionRelativeToScreen = position - centerScreen;
		double positionRelativeToScreenCm = positionRelativeToScreen / getHorizontalFactorCamera();

		double deltaYWithTarget = positionRelativeToScreenCm;

		double offsetCenter = getOffsetCameraToTargetCenterReal().getX();
		Point2D cameraPosition = getCameraPosition();
		Point2D targetPositionEstimated = new Point2D(0, offsetCenter + deltaYWithTarget);
		return targetPositionEstimated.subtract(cameraPosition).normalize();

	}

	@Override
	public Point2D getCameraPosition() {
		double offsetCenter = getOffsetCameraToTargetCenterReal().getX();
		return new Point2D(distanceCamera, offsetCenter);
	}
}
