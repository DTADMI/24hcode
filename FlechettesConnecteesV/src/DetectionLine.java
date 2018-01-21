import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;

public class DetectionLine {
	private String id;

	private final List<Point2D> points = new ArrayList<>();

	public DetectionLine() {
	}

	public DetectionLine(String id) {
		this();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Point2D> getPoints() {
		return points;
	}
}
