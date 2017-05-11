package de.uni_oldenburg.simulation.supermarket;

/**
 * Starts the Mason GUI for our simulation.
 * This is a starter for SupermarketWithUI because you can't run its main class directly.
 * See http://stackoverflow.com/questions/29366373/maven-error-could-not-find-or-load-main-class#answer-38133937
 */
public class AppStarter {

	/**
	 * Enter the program here
	 */
	public static void main(String[] args) {
		new SupermarketWithUI().createController();
	}
}