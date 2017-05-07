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

			// Look forward in every direction
			Bag [] objectsAtNextLocation = new Bag [Supermarket.GRID_WIDTH];
			for (int i = 0; i < Supermarket.GRID_WIDTH; i++) {
				objectsAtNextLocation[i] = supermarket.customerGrid.getObjectsAtLocation((location.x + i) % Supermarket.GRID_WIDTH, location.y + 1);
			}

			// Step forward or to next free place to the left if possible (but no self checkout)
			for (int i = 0; i < Supermarket.GRID_WIDTH; i++) {
				if (objectsAtNextLocation[i] == null && location.y != Supermarket.CHECKOUT_POSITION_Y) {
					// TODO take other params into account
					supermarket.customerGrid.setObjectLocation(this, (location.x + i) % Supermarket.GRID_WIDTH, location.y + 1);
				}
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