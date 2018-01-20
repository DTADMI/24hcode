package model;

public class Zone {

	private double distanceMinCentre;
	private double distanceMaxCentre;
	private Arc arc;
	
	/**
	 * @param distanceMinCentre
	 * @param distanceMaxCentre
	 * @param angleArete1
	 * @param angleArete2
	 */
	public Zone(double distanceMinCentre, double distanceMaxCentre, double angleArete1, double angleArete2) {
		super();
		this.distanceMinCentre = distanceMinCentre;
		this.distanceMaxCentre = distanceMaxCentre;
		this.arc.setAngleArete1(angleArete1);
		this.arc.setAngleArete2(angleArete2);
	}
	
	/**
	 * @param distanceMinCentre
	 * @param distanceMaxCentre
	 * @param arc
	 */
	public Zone(double distanceMinCentre, double distanceMaxCentre, Arc arc) {
		super();
		this.distanceMinCentre = distanceMinCentre;
		this.distanceMaxCentre = distanceMaxCentre;
		this.arc = arc;
	}
	/**
	 * @return the distanceMinCentre
	 */
	public double getDistanceMinCentre() {
		return distanceMinCentre;
	}
	/**
	 * @param distanceMinCentre the distanceMinCentre to set
	 */
	public void setDistanceMinCentre(double distanceMinCentre) {
		this.distanceMinCentre = distanceMinCentre;
	}
	/**
	 * @return the distanceMaxCentre
	 */
	public double getDistanceMaxCentre() {
		return distanceMaxCentre;
	}
	/**
	 * @param distanceMaxCentre the distanceMaxCentre to set
	 */
	public void setDistanceMaxCentre(double distanceMaxCentre) {
		this.distanceMaxCentre = distanceMaxCentre;
	}
	
	/**
	 * @return the arc
	 */
	public Arc getArc() {
		return arc;
	}
	/**
	 * @param arc the arc to set
	 */
	public void setArc(Arc arc) {
		this.arc = arc;
	}
	
	public boolean containsCoordinates(double r, double theta) {
		return (r<distanceMaxCentre && r>=distanceMinCentre && ( ( (theta<9 || theta>=351) && (theta>=0 || theta<360) ) ||  theta<arc.getAngleArete2() && theta>=arc.getAngleArete1())) ? true : false;
	}
	
	public String toString() {
		return "Zone : \n\t distance minimale au centre : " + distanceMinCentre + "\n\t distance maximale au centre : " + distanceMaxCentre + "\n\t Arc : " + arc.toString();
	}
	
}
