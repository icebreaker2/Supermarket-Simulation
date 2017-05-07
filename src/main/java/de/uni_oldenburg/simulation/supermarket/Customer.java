package de.uni_oldenburg.simulation.supermarket;

import sim.portrayal.*;
import sim.portrayal.simple.*;
import sim.util.*;
import sim.engine.*;

import java.awt.*;

/**
 * This is our model for a single customer (agent) waiting in the supermarket queue
 */
public class Customer extends OvalPortrayal2D implements Steppable {

	public Customer() {
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

			// Look forward
			Bag objectsAtNextLocation = supermarket.customerGrid.getObjectsAtLocation(location.x, location.y + 1); // TODO switch x location if checkout is overfilled

			// Step forward if possible (but no self checkout)
			if (objectsAtNextLocation == null && location.y != supermarket.CHECKOUT_POSITION_Y) {
				supermarket.customerGrid.setObjectLocation(this, location.x, location.y + 1); // TODO switch x location if checkout is overfilled
			}
		}
	}

	/**
	 * Draw the customer
	 *
	 * @param object The customer
	 * @param graphics The drawing
	 * @param info Infos about the canvas
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
}