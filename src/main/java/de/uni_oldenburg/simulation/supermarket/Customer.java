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

	// Characteristics
	private boolean infirm;
	private boolean stressed;
	private boolean prefersCheckoutOne;

	private Supermarket supermarket;

	public Customer(Supermarket supermarket) {
		this.supermarket = supermarket;

		// Decide characteristics
		this.infirm = supermarket.random.nextBoolean(supermarket.getCustomerInfirm_probability());
		this.stressed = supermarket.random.nextBoolean(supermarket.getCustomerStressed_probability());
		this.prefersCheckoutOne = supermarket.random.nextBoolean(supermarket.getCustomerPrefersCheckoutOne_probability());
	}

	/**
	 * Act according to the current environment
	 *
	 * @param state The current state of the supermarket
	 */
	public void step(final SimState state) {

		Int2D location = supermarket.customerGrid.getObjectLocation(this);

		// Customer still in the supermarket?
		if (location != null) {

			// Look forward
			Bag objectsAtNextLocation = supermarket.customerGrid.getObjectsAtLocation(location.x, location.y + 1);

			// No self checkout
			if (location.y != supermarket.CHECKOUT_POSITION_Y) {

				// Step forward whenever possible
				if (objectsAtNextLocation == null) {
					supermarket.customerGrid.setObjectLocation(this, location.x, location.y + 1);
				} else {

					if (wantsToChangeQueue()) {

						// Check sides
						if (supermarket.random.nextBoolean()) {
							// Check left
							Bag objectsAtLeftLocation = supermarket.customerGrid.getObjectsAtLocation(location.x-1, location.y );
							if (objectsAtLeftLocation == null) {
								supermarket.customerGrid.setObjectLocation(this, location.x-1, location.y);
							}
						} else {
							// Check right
							Bag objectsAtRightLocation = supermarket.customerGrid.getObjectsAtLocation(location.x+1, location.y );
							if (objectsAtRightLocation == null) {
								supermarket.customerGrid.setObjectLocation(this, location.x+1, location.y );
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Get random checkout time with given mean, variance and characteristics
	 *
	 * @return Random time a customer needs for checkout
	 */
	public int getCheckoutTime() {

		int timeBonus = 0;
		if (infirm) timeBonus = 10; // Infirm people are slower

		// no negative numbers allowed
		return (int) Math.max(Math.round(supermarket.random.nextGaussian() * supermarket.getCheckoutProcessingTime_variance() + supermarket.getCheckoutProcessingTime_mean() + timeBonus), 0);
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
	 * Computes the willingness of the customer to switch the checkout queue. Takes characteristics into account
	 *
	 * @return a boolean value indicating whether the customer is willing to switch to another queue (1) or not (0)
	 */
	private boolean wantsToChangeQueue() {

		Int2D location = supermarket.customerGrid.getObjectLocation(this);
		boolean isWaitingAtCheckoutOne = (location.x == 0);

		int wantsToChangeScore = 0;
		if (prefersCheckoutOne && !isWaitingAtCheckoutOne) wantsToChangeScore++;
		if (stressed) wantsToChangeScore++;
		if (!infirm) wantsToChangeScore++;

		return supermarket.random.nextBoolean(wantsToChangeScore/3);
	}
}