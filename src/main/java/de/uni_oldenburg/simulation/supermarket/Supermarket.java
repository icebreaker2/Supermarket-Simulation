package de.uni_oldenburg.simulation.supermarket;

import sim.engine.*;
import sim.field.grid.*;

/**
 * This is our simulation model for a supermarket.
 */
public class Supermarket extends SimState {

	// set amount of checkouts
	private final int checkoutAmount = 4;

	// set y locations for checkout and spawn point of the customers
	public static final int SPAWN_POSITION_Y = 0;
	public static final int CHECKOUT_POSITION_Y = 49;

	// set x locations for checkout and spawn point of the customers
	public static final int FIRST_SPAWN_POSITION_X = 0;
	public static final int FIRST_CHECKOUT_POSITION_X = 0;
	public static final int SECOND_SPAWN_POSITION_X = 1;
	public static final int SECOND_CHECKOUT_POSITION_X = 1;
	public static final int THIRD_SPAWN_POSITION_X = 2;
	public static final int THIRD_CHECKOUT_POSITION_X = 2;
	public static final int FOURTH_SPAWN_POSITION_X = 3;
	public static final int FOURHT_CHECKOUT_POSITION_X = 3;

	// Sites
	public static final int SPAWN_POINT_ID = 1;
	public static final int CHECKOUT_POINT_ID = 2;

	// some initial properties
	private double checkoutCustomersAmount_mean = 12.0;
	private double checkoutCustomersAmount_variance = 4.0;
	private double checkoutProcessingTime_mean = 30.0;
	private double checkoutProcessingTime_variance = 5.0;
	private int totalCustomersAmount = 0;


	public IntGrid2D supermarketMap;
	public SparseGrid2D customerGrid;

	public static final int GRID_HEIGHT = 50;
	public static final int GRID_WIDTH = 4;

	Customer customerAtCheckout; // Just a reference

	public Supermarket(long seed) {
		super(seed);
	}

	/**
	 * Start the simulation
	 */
	public void start() {
		super.start();  // clear out the schedule

		// Initialize grids
		supermarketMap = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);
		customerGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);

		// Draw supermarketMap
		for(int i = 0; i < GRID_WIDTH; i++) {
			supermarketMap.field[i][SPAWN_POSITION_Y] = SPAWN_POINT_ID;
			supermarketMap.field[i][CHECKOUT_POSITION_Y] = CHECKOUT_POINT_ID;
		}

		// Dynamically spawn new customers
		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {

			// Synchronize customer spawn with checkout queue
			if (schedule.getSteps() % Math.round(Math.sqrt(checkoutProcessingTime_mean)) == 0) {

				// Add new customers randomly
				if (newCustomerArrived()) {
					Customer customer = new Customer();
					totalCustomersAmount++;
					int [] spawnLocation = createSpawnLocation(); // x,y coordinates
					customerGrid.setObjectLocation(customer, spawnLocation[0], spawnLocation[1]);
					schedule.scheduleRepeating(customer, 1);
				}
			}
		}, 1);

		// Checkout customers at the checkout
		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {

			// New customer arrived at checkout
			for(int i = 0; i < GRID_WIDTH; i++) {
				if (customerGrid.getObjectsAtLocation(i, CHECKOUT_POSITION_Y) != null) {
					Customer customer = (Customer) customerGrid.getObjectsAtLocation(i, CHECKOUT_POSITION_Y).get(0);
					if (customer != customerAtCheckout) {
						schedule.scheduleOnceIn(getCheckoutTime(), (Steppable) (SimState leaveState) -> {
							customerGrid.remove(customer);
						});
						customerAtCheckout = customer;
					}
				}
			}
		}, 1);
	}

	/**
	 * Randomly chooses a spawn location for any new customer.
	 * @return x and y coordinates of the customers spawn point
	 */
	private int[] createSpawnLocation() {
		return new int[]{(int)(random.nextDouble() % checkoutAmount), (int)(random.nextDouble() % checkoutAmount)};
	}

	/**
	 * Randomly decides if a new customer arrived.
	 * Adds a drift to gain given mean and variance.
	 *
	 * @return new customer to be added
	 */
	private boolean newCustomerArrived() {
		if (checkoutCustomersAmount_mean-checkoutCustomersAmount_variance > random.nextGaussian()*checkoutCustomersAmount_variance+getNumberOfWaitingCustomers()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get random checkout time with given mean and variance
	 *
	 * @return Random time a customer needs for checkout
	 */
	private int getCheckoutTime() {
		int checkoutTime = (int) Math.round(random.nextGaussian()*checkoutProcessingTime_variance+checkoutProcessingTime_mean);

		// Checkout time must be positive
		if (checkoutTime > 0) {
			return checkoutTime;
		} else {
			return 1;
		}
	}

	/**
	 * @return The variance of the queue length
	 */
	public double getCheckoutCustomersAmount_variance() {
		return checkoutCustomersAmount_variance;
	}

	/**
	 * @param checkoutCustomersAmount_variance The variance of the queue length
	 */
	public void setCheckoutCustomersAmount_variance(double checkoutCustomersAmount_variance) {
		this.checkoutCustomersAmount_variance = checkoutCustomersAmount_variance;
	}

	/**
	 * @return The mean of the queue length
	 */
	public double getCheckoutCustomersAmount_mean() {
		return checkoutCustomersAmount_mean;
	}

	/**
	 * @param checkoutCustomersAmount_mean The variance of the queue length
	 */
	public void setCheckoutCustomersAmount_mean(double checkoutCustomersAmount_mean) {
		this.checkoutCustomersAmount_mean = checkoutCustomersAmount_mean;
	}

	/**
	 * @return The time variance for a single checkout
	 */
	public double getCheckoutProcessingTime_variance() {
		return checkoutProcessingTime_variance;
	}

	/**
	 * @param checkoutProcessingTime_variance The time variance for a single checkout
	 */
	public void setCheckoutProcessingTime_variance(double checkoutProcessingTime_variance) {
		this.checkoutProcessingTime_variance = checkoutProcessingTime_variance;
	}

	/**
	 * @return The mean time for a single checkout
	 */
	public double getCheckoutProcessingTime_mean() {
		return checkoutProcessingTime_mean;
	}

	/**
	 * @param checkoutProcessingTime_mean The mean time for a single checkout
	 */
	public void setCheckoutProcessingTime_mean(double checkoutProcessingTime_mean) {
		this.checkoutProcessingTime_mean = checkoutProcessingTime_mean;
	}

	/**
	 * @return The number of customers in the supermarket
	 */
	public int getNumberOfWaitingCustomers() {
		if (customerGrid != null) {
			return customerGrid.getAllObjects().size();
		} else {
			return 0;
		}
	}

	/**
	 * @return The total number of customers since simulation start
	 */
	public int getTotalCustomersAmount() {
		return totalCustomersAmount;
	}
}