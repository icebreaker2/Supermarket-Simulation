package de.uni_oldenburg.simulation.supermarket;

/**
 * There is one reason for this class and one reason only. You cannot extend the class providing the main class with something exotic like GUIState in our case. See http://stackoverflow.com/questions/29366373/maven-error-could-not-find-or-load-main-class#answer-38133937
 */
public class Main {

	/**
	 * Enter the program here
	 */
	public static void main(String[] args) {
		new SupermarketWithUI().createController();
	}
}
