package de.uni_oldenburg.simulation.supermarket.customers;

import de.uni_oldenburg.simulation.supermarket.Supermarket;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * The RandomStrategyCustomer may randomly switch rows according to his characteristics
 */
public class RandomStrategyCustomer extends Customer {

	public String name = "Random";

	public RandomStrategyCustomer(Supermarket supermarket) {
		super(supermarket);
	}

	@Override
	public void executeStrategyStep(Supermarket supermarket) {

		// Look forward
		Bag objectsAtNextLocation = supermarket.customerGrid.getObjectsAtLocation(location.x, location.y + 1);

		// Step forward whenever possible
		if (objectsAtNextLocation == null) {
			supermarket.customerGrid.setObjectLocation(this, location.x, location.y + 1);
		} else {

			if (wantsToChangeQueue()) {

				// Check sides
				if (location.x == 0) {
					// Check right
					Bag objectsAtRightLocation = supermarket.customerGrid.getObjectsAtLocation(location.x+1, location.y );
					if (objectsAtRightLocation == null) {
						supermarket.customerGrid.setObjectLocation(this, location.x+1, location.y );
					}
				} else if (location.x == supermarket.GRID_WIDTH-1) {
					// Check left
					Bag objectsAtLeftLocation = supermarket.customerGrid.getObjectsAtLocation(location.x-1, location.y );
					if (objectsAtLeftLocation == null) {
						supermarket.customerGrid.setObjectLocation(this, location.x-1, location.y);
					}
				} else if (supermarket.random.nextBoolean()) {
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