/**
 * 
 */
package controller;

import java.util.ArrayList;
import java.util.Iterator;

import model.Arc;
import model.Cible;
import model.CoordonneesCartesiennes;
import model.CoordonneesPolaires;
import model.Zone;

/**
 * @author Darryl
 *
 */
public class IA {

	public Cible cibleBuilder(double[] rayons) {
		Cible cible = new Cible();
		int[] points = { 20, 1, 18, 4, 13, 6, 10, 15, 2, 17, 3, 19, 7, 16, 8, 11, 14, 9, 12, 5 };
		// rayon0 = 0.635; rayon1 = 1.59

		ArrayList<Arc> arcs = new ArrayList<>();
		arcs.add(new Arc(351, 9));
		double angle0 = 9;
		for (int i = 1; i < 20; i++) {
			arcs.add(new Arc(angle0, angle0 + 18));
			angle0 += 18;
		}

		Zone zone0 = new Zone(0, rayons[0], new Arc(0, 360));
		ArrayList<Zone> zones0 = new ArrayList<>();
		zones0.add(zone0);
		cible.getCercle().put(50, zones0);
		Zone zone1 = new Zone(rayons[0], rayons[1], new Arc(0, 360));
		ArrayList<Zone> zones1 = new ArrayList<>();
		zones1.add(zone1);
		cible.getCercle().put(25, zones1);

		double distanceMin = rayons[1];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 20; j++) {
				Integer pointZone = points[j];
				if (i == 1) {
					pointZone *= 3;
				} else if (i == 3) {
					pointZone *= 2;
				}
				if (!cible.getCercle().containsKey(pointZone)) {
					cible.getCercle().put(pointZone, new ArrayList<Zone>());
				}

				ArrayList<Zone> zonesj = cible.getCercle().get(pointZone);
				zonesj.add(new Zone(distanceMin, rayons[2 + i], arcs.get(j)));
			}
			distanceMin = rayons[2 + i];
		}

		return cible;
	}

	public int getPoints(double x, double y, Cible cible) {
		int points = 0;

		CoordonneesPolaires cp = cartesianToPolar(new CoordonneesCartesiennes(x, y));
		Integer point;
		// for(Integer point : cible.getCercle().keySet())
		Iterator<Integer> pointsKeys = cible.getCercle().keySet().iterator();
		while (points == 0 && pointsKeys.hasNext()) {
			point = pointsKeys.next();
			ArrayList<Zone> zones = cible.getCercle().get(point);
			for (Zone zone : zones) {
				if (zone.containsCoordinates(cp.getDistanceCentre(), cp.getAngle())) {
					points = point;
					break;
				}
			}
		}

		return points;
	}

	public CoordonneesPolaires cartesianToPolar(CoordonneesCartesiennes cc) {

		CoordonneesPolaires cp = new CoordonneesPolaires();
		cp.setDistanceCentre(Math.sqrt(cc.getX() * cc.getX() + cc.getY() * cc.getY()));
		cp.setAngle(Math.atan2(cc.getY(), cc.getX()));

		System.out.println("Distance au centre     = " + cp.getDistanceCentre());
		System.out.println("Angle = " + cp.getAngle());

		return cp;
	}
}
