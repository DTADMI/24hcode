package coordinatesMapper.controller;

import java.util.InputMismatchException;
import java.util.Scanner;

import coordinatesMapper.model.Cible;

public class Competition {

	public static void main(String args[]) {
		IA sonny = new IA();
		double[] rayons = { 0.635, 1.59, 10.74 - 0.8, 10.74, 17 - 0.8, 17 };
		Cible cible = sonny.cibleBuilder(rayons);
		System.out.println(cible.toString() + "\n\n");

		System.out.println("\n Points : " + jouerTouer(3, sonny, cible));
	}

	public static int jouerTouer(int nbFleches, IA ia, Cible cible) {
		int pointsTour = 0;
		System.out.println("Début du tour : \n");
		Scanner scan = new Scanner(System.in);
		for (int i = 0; i < nbFleches; i++) {
			double in_x;
			double in_y;
			try {
				System.out.println("Entrez l'abscisse de la flèche : ");
				in_x = scan.nextDouble();

				System.out.println("Entrez l'ordonnée de la flèche : ");
				in_y = scan.nextDouble();

				pointsTour += ia.getPoints(in_x, in_y, cible);
			} catch (NumberFormatException | InputMismatchException e) {
				System.out.println("Lancer non valide");
				continue;
			}
		}

		scan.close();

		return pointsTour;
	}

}