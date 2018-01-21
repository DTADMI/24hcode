/**
 * 
 */
package coordinatesMapper.model;

/**
 * @author Darryl
 *
 */
public class CoordonneesPolaires {
	double angle;
	double distanceCentre;
	/**
	 * 
	 */
	public CoordonneesPolaires() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param angle
	 * @param distanceCentre
	 */
	public CoordonneesPolaires(double angle, double distanceCentre) {
		super();
		this.angle = angle;
		this.distanceCentre = distanceCentre;
	}
	/**
	 * @return the angle
	 */
	public double getAngle() {
		return angle;
	}
	/**
	 * @param angle the angle to set
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}
	/**
	 * @return the distanceCentre
	 */
	public double getDistanceCentre() {
		return distanceCentre;
	}
	/**
	 * @param distanceCentre the distanceCentre to set
	 */
	public void setDistanceCentre(double distanceCentre) {
		this.distanceCentre = distanceCentre;
	}
	
	public String toString() {
		return "Coordonnées Polaires : \n\t angle : " + angle + "\n\t distance au centre : " + distanceCentre;
	}
}
