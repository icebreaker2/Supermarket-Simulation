package de.uni_oldenburg.simulation.supermarket.customers;

import de.uni_oldenburg.simulation.supermarket.Supermarket;
import sim.util.Bag;

/**
 * The basic customer only goes forward whenever possible
 */
public class OnlyGoForwardCustomer extends Customer {

	/**
	 * Constructor
	 * @param supermarket
	 */
	public OnlyGoForwardCustomer(Supermarket supermarket) {
		super(supermarket);
	}

	/**
	 * The basic customer only goes forward whenever possible
	 */
	@Override
	public void executeStrategyStep(Supermarket supermarket) {

		// Look forward
		Bag objectsAtNextLocation = supermarket.customerGrid.getObjectsAtLocation(location.x, location.y + 1);

		// The basic Customer only goes forward whenever possible
		if (objectsAtNextLocation == null) {
			supermarket.customerGrid.setObjectLocation(this, location.x, location.y + 1);
		}
	}
}