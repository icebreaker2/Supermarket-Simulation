package de.uni_oldenburg.simulation.supermarket.customers;

import de.uni_oldenburg.simulation.supermarket.Supermarket;
import sim.util.Bag;

/**
 * The RandomStrategyCustomer may randomly switch rows according to his characteristics
 */
public class RandomStrategyCustomer extends Customer {

	public RandomStrategyCustomer(Supermarket supermarket) {
		super(supermarket);
	}

	public void executeStrategyStep() {
		// Look forward
		Bag objectsAtNextLocation = supermarket.customerGrid.getObjectsAtLocation(location.x, location.y + 1);

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

						//System.out.println("Go left");
					}
				} else {
					// Check right
					Bag objectsAtRightLocation = supermarket.customerGrid.getObjectsAtLocation(location.x+1, location.y );
					if (objectsAtRightLocation == null) {
						supermarket.customerGrid.setObjectLocation(this, location.x+1, location.y);

						//System.out.println("Go right");
					}
				}
			}
		}
	}
}