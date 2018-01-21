/**
 *
 */
package coordinatesMapper.model;

/**
 * @author Darryl
 *
 */
public class Arc {

	private double angleArete1;
	private double angleArete2;

	/**
	 * @param angleArete1
	 * @param angleArete2
	 */
	public Arc(double angleArete1, double angleArete2) {
		super();
		this.angleArete1 = angleArete1;
		this.angleArete2 = angleArete2;
	}
	/**
	 * @return the angleArete1
	 */
	public double getAngleArete1() {
		return angleArete1;
	}
	/**
	 * @param angleArete1 the angleArete1 to set
	 */
	public void setAngleArete1(double angleArete1) {
		this.angleArete1 = angleArete1;
	}
	/**
	 * @return the angleArete2
	 */
	public double getAngleArete2() {
		return angleArete2;
	}
	/**
	 * @param angleArete2 the angleArete2 to set
	 */
	public void setAngleArete2(double angleArete2) {
		this.angleArete2 = angleArete2;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Arc [angleArete1=" + angleArete1 + ", angleArete2=" + angleArete2 + "]";
	}


}
