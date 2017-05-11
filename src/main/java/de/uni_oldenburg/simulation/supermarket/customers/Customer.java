package de.uni_oldenburg.simulation.supermarket.customers;

import de.uni_oldenburg.simulation.supermarket.Supermarket;
import sim.portrayal.*;
import sim.portrayal.simple.*;
import sim.util.*;
import sim.engine.*;

import java.awt.*;

/**
 * This is our model for a single customer (agent) waiting in the supermarket queue
 */
public abstract class Customer extends OvalPortrayal2D implements Steppable, DistributionStrategy {

	// Characteristics
	private boolean infirm;
	private boolean stressed;
	private boolean prefersCheckoutOne;
	public String name;

	public Supermarket supermarket;
	public Int2D location;

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

		// Update location
		location = supermarket.customerGrid.getObjectLocation(this);

		// Update shortest queue


		// Customer still in the supermarket?
		if (location != null) {

			// No self checkout
			if (location.y != supermarket.CHECKOUT_POSITION_Y) {
				executeStrategyStep(supermarket);
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

		// Set customer colors
		graphics.setColor(Color.black);
		if (infirm) graphics.setColor(Color.yellow);
		if (stressed) graphics.setColor(Color.red);
		if (stressed && infirm) graphics.setColor(Color.orange);

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
	public boolean wantsToChangeQueue() {

		Int2D location = supermarket.customerGrid.getObjectLocation(this);
		boolean isWaitingAtCheckoutOne = (location.x == 0);

		int wantsToChangeScore = 0;
		if (prefersCheckoutOne && !isWaitingAtCheckoutOne) wantsToChangeScore++;
		if (stressed) wantsToChangeScore++;
		if (!infirm) wantsToChangeScore++;

		return supermarket.random.nextBoolean(Math.min(wantsToChangeScore/2, 1));
	}
}