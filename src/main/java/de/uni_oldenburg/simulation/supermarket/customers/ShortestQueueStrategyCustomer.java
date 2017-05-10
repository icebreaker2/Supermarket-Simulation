package de.uni_oldenburg.simulation.supermarket.customers;

import de.uni_oldenburg.simulation.supermarket.Supermarket;
import sim.util.Bag;

/**
 * The ShortestQueueStrategyCustomer only switches the queue in the direction to the shortest queue
 */
public class ShortestQueueStrategyCustomer extends Customer {

	public ShortestQueueStrategyCustomer(Supermarket supermarket) {
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
				int shortestQueue = supermarket.getShortestQueue();

				if (shortestQueue < location.x) {
					// Check left
					Bag objectsAtLeftLocation = supermarket.customerGrid.getObjectsAtLocation(location.x - 1, location.y);
					if (objectsAtLeftLocation == null) {
						supermarket.customerGrid.setObjectLocation(this, location.x - 1, location.y);
					}
				} else if (shortestQueue > location.x) {
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