package de.uni_oldenburg.simulation.supermarket;

import ec.util.MersenneTwisterFast;
import sim.portrayal.*;
import sim.portrayal.simple.*;
import sim.util.*;
import sim.engine.*;

import java.awt.*;

/**
 * This is our model for a single customer (agent) waiting in the supermarket queue
 */
public class Customer extends OvalPortrayal2D implements Steppable {

	private int age;
	private double stressLevel;
	private int lovesCashierAtCheckoutX;

	public Customer(int age, double stressLevel, int lovesCashierAtCheckoutX) {
		this.age = age;
		this.stressLevel = stressLevel;
		this.lovesCashierAtCheckoutX = lovesCashierAtCheckoutX;
	}

	/**
	 * Act according to the current environment
	 *
	 * @param state The current state of the supermarket
	 */
	public void step(final SimState state) {
		Supermarket supermarket = (Supermarket) state;

		Int2D location = supermarket.customerGrid.getObjectLocation(this);

		// Customer still in the supermarket?
		if (location != null) {

			// Look forward in every direction
			Bag[] objectsAtNextLocation = new Bag[Supermarket.GRID_WIDTH];
			for (int i = 0; i < Supermarket.GRID_WIDTH; i++) {
				objectsAtNextLocation[i] = supermarket.customerGrid.getObjectsAtLocation((location.x + i) % Supermarket.GRID_WIDTH, location.y + 1);
			}

			// Step forward or to next free place to the left if possible (but no self checkout)
			if (location.y != Supermarket.CHECKOUT_POSITION_Y) {
				if (objectsAtNextLocation[location.x] == null) { // step forward
					supermarket.customerGrid.setObjectLocation(this, location.x, location.y + 1);
				} else { // decide to switch the queue
					for (int i = 0; i < Supermarket.GRID_WIDTH; i++) {
						boolean isWillingToSwitchQueue = computeWillingness(supermarket, location.x, (location.x + i) % Supermarket.GRID_WIDTH);
						if (isWillingToSwitchQueue && objectsAtNextLocation[(location.x + i) % Supermarket.GRID_WIDTH] == null) {
							supermarket.customerGrid.setObjectLocation(this, (location.x + i) % Supermarket.GRID_WIDTH, location.y + 1);
						}
					}
				}
			}
		}
	}

	/**
	 * Draw the customer
	 *
	 * @param object   The customer
	 * @param graphics The drawing
	 * @param info     Infos about the canvas
	 */
	public final void draw(Object object, Graphics2D graphics, DrawInfo2D info) {

		graphics.setColor(Color.black);
		// this code was stolen from OvalPortrayal2D
		int x = (int) (info.draw.x - info.draw.width / 2.0);
		int y = (int) (info.draw.y - info.draw.height / 2.0);
		int width = (int) (info.draw.width);
		int height = (int) (info.draw.height);
		graphics.fillOval(x, y, width, height);
	}

	/**
	 * Computes the willingness of the customer to switch the checkout queue. Takes age and stressLevel into account
	 *
	 * @param supermarket      is the supermarket of the customer
	 * @param currentXLocation is the current x coordinate of the customer
	 * @param nextXLocation    is the next location to switch at
	 * @return a boolean value indicating whether the customer is willing to switch to another queue (1) or not (0)
	 */
	private boolean computeWillingness(Supermarket supermarket, int currentXLocation, int nextXLocation) {
		int shortesQueue = supermarket.getShortestQueue();
		if (age >= supermarket.getCustomersAge_mean()) { // old people are calm
			if (stressLevel <= supermarket.getCustomersStressLevel_mean() && lovesCashierAtCheckoutX == currentXLocation) {
				return false;
			} else if (lovesCashierAtCheckoutX == nextXLocation) {
				return (new MersenneTwisterFast().nextDouble() >= 0.5); // throw dice to decide, old people do not need love by all means
			} else {
				return nextXLocation == shortesQueue; // else all go to the shortest one
			}
		} else { // do not hesitate
			if (lovesCashierAtCheckoutX == currentXLocation) {
				return false;
			} else if (stressLevel > supermarket.getCustomersStressLevel_mean()) {
				return true;
			} else if (lovesCashierAtCheckoutX == nextXLocation) {
				return true;
			} else {
				return nextXLocation == shortesQueue; // else all go to the shortest one
			}
		}
	}

	/**
	 * Computes the customers age normal deviated given by the mean and variance passed
	 *
	 * @param mean     age of the customers
	 * @param variance of the normal distribution for the customers age
	 * @return the normal deviated age
	 */
	public static int computeAgeNormalDeviated(double mean, double variance) {
		return (int) (new MersenneTwisterFast().nextGaussian() * variance + mean); // no negative numbers possible
	}

	/**
	 * Computes the customers stress level normal deviated given by the mean and variance passed
	 *
	 * @param mean     stress level of the customers
	 * @param variance of the normal distribution of the customers stress level
	 * @return the normal deviated stress level
	 */
	public static double computeStressLevelNormalDeviated(double mean, double variance) {
		return (int) (new MersenneTwisterFast().nextGaussian() * variance + mean); // no negative numbers possible
	}

	/**
	 * Computes the customers love for one of the cashier normal deviated given by the mean and variance passed
	 *
	 * @param mean     love of the customer for cashier at checkout x
	 * @param variance of the normal distribution of the customers love for the cashier at checkout x
	 * @return the normal deviated cashier the customers fell in love with
	 */
	public static int computelovesCashierAtCheckoutXNormalDeviated(double mean, double variance) {
		return (int) (new MersenneTwisterFast().nextGaussian() * variance + mean) % Supermarket.GRID_WIDTH; // no negative numbers possible
	}
}