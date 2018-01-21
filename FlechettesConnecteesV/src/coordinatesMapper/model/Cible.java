/**
 *
 */
package coordinatesMapper.model;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author Darryl
 *
 */
public class Cible {

	private Hashtable<Integer, ArrayList<Zone>> cercle;

	/**
	 *
	 */
	public Cible() {
		super();
		// TODO Auto-generated constructor stub
		cercle = new Hashtable<>();
	}

	/**
	 * @param cercle
	 */
	public Cible(Hashtable<Integer, ArrayList<Zone>> cercle) {
		super();
		this.cercle = cercle;
	}

	/**
	 * @return the cercle
	 */
	public Hashtable<Integer, ArrayList<Zone>> getCercle() {
		return cercle;
	}

	/**
	 * @param cercle the cercle to set
	 */
	public void setCercle(Hashtable<Integer, ArrayList<Zone>> cercle) {
		this.cercle = cercle;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Cible [cercle=" + cercle + "]";
	}


}
