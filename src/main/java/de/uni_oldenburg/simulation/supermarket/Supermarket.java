package de.uni_oldenburg.simulation.supermarket;

import sim.engine.*;
import sim.field.grid.*;

/**
 * This is our simulation model for a supermarket.
 */
public class Supermarket extends SimState {

	// set y locations for checkout and spawn point of the customers
	public static final int SPAWN_POSITION_Y = 0;
	public static final int CHECKOUT_POSITION_Y = 49;

	// Sites
	public static final int SPAWN_POINT_ID = 1;
	public static final int CHECKOUT_POINT_ID = 2;

	// some initial properties
	private double checkoutCustomersAmount_mean = 12.0;
	private double checkoutCustomersAmount_variance = 4.0;
	private double checkoutProcessingTime_mean = 30.0;
	private double checkoutProcessingTime_variance = 5.0;

	private double customersAge_mean = 40.0;
	private double customersAge_variance = 4.0;
	private double customersStressLevel_mean = 5.0;
	private double customersStressLevel_variance = 1.5;

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
		customerGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
		supermarketMap = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);

		// Draw supermarketMap
		for (int i = 0; i < GRID_WIDTH; i++) {
			supermarketMap.field[i][SPAWN_POSITION_Y] = SPAWN_POINT_ID;
			supermarketMap.field[i][CHECKOUT_POSITION_Y] = CHECKOUT_POINT_ID;
		}

		// Dynamically spawn new customers
		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {

			double wantedProbability = this.random.nextDouble(true, true);
			int maxNumberOfX = 100000; // this is actually not a max number of customers but a highly unlikely number of customers
			int newCustomers = Auxiliary.solveNormalDeviationForX(checkoutCustomersAmount_mean, checkoutCustomersAmount_variance, wantedProbability, maxNumberOfX);

			for (int i = 0; i < newCustomers; i++) {
				// Add new customers randomly
				if (newCustomerArrived()) {
					// set customers properties and the customer itself
					Customer customer = new Customer(Customer.computeAgeNormalDeviated(customersAge_mean, customersAge_variance), Customer.computeStressLevelNormalDeviated(customersStressLevel_mean, customersStressLevel_variance), 0);
					totalCustomersAmount++;
					int[] spawnLocation = createSpawnLocation(); // x,y coordinates
					customerGrid.setObjectLocation(customer, spawnLocation[0], spawnLocation[1]);
					schedule.scheduleRepeating(customer, 1);
				}
			}
		}, 1);

		// Checkout customers at the checkout
		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {

			// New customer arrived at checkout
			for (int i = 0; i < GRID_WIDTH; i++) {
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
	 *
	 * @return x and y coordinates of the customers spawn point
	 */
	private int[] createSpawnLocation() {
		return new int[]{(int) (random.nextDouble() * GRID_WIDTH % GRID_WIDTH), (int) (random.nextDouble() * GRID_WIDTH % GRID_WIDTH)};
	}

	/**
	 * Randomly decides if a new customer arrived.
	 * Adds a drift to gain given mean and variance.
	 *
	 * @return new customer to be added
	 */
	private boolean newCustomerArrived() {
		if (checkoutCustomersAmount_mean - checkoutCustomersAmount_variance > random.nextGaussian() * checkoutCustomersAmount_variance + getNumberOfWaitingCustomers()) {
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
		int checkoutTime = (int) Math.round(random.nextGaussian() * checkoutProcessingTime_variance + checkoutProcessingTime_mean);
		// TODO change to normal deviation for X ?!
		// Checkout time must be positive
		if (checkoutTime > 0) {
			return checkoutTime;
		} else {
			return 1;
		}
	}

	/**
	 * Computes the shortest queue of all and return its x coordinate
	 * @return the x coordinate of the shortest queue or -1 in case of any error
	 */
	public int getShortestQueue() {
		int[] queuesAtX = new int[GRID_WIDTH];
		if (customerGrid != null) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				for (int y = GRID_HEIGHT - 1; y >= 0; y--) {
					if (customerGrid.getObjectsAtLocation(x, y) != null) {
						queuesAtX[x]++;
					} else {
						break; // no more customers in the queue
					}
				}
			}
			int shortestQueue = (int) Math.min(Math.min(queuesAtX[0], queuesAtX[1]), Math.min(queuesAtX[2], queuesAtX[3]));
			for (int i = 0; i < GRID_WIDTH; i++) {
				if (queuesAtX[i] == shortestQueue) return i;
			}
		}
		return -1;
	}

	public double getCustomersAge_mean() {
		return customersAge_mean;
	}

	public void setCustomersAge_mean(double customersAge_mean) {
		this.customersAge_mean = customersAge_mean;
	}

	public double getCustomersAge_variance() {
		return customersAge_variance;
	}

	public void setCustomersAge_variance(double customersAge_variance) {
		this.customersAge_variance = customersAge_variance;
	}

	public double getCustomersStressLevel_mean() {
		return customersStressLevel_mean;
	}

	public void setCustomersStressLevel_mean(double customersStressLevel_mean) {
		this.customersStressLevel_mean = customersStressLevel_mean;
	}

	public double getCustomersStressLevel_variance() {
		return customersStressLevel_variance;
	}

	public void setCustomersStressLevel_variance(double customersStressLevel_variance) {
		this.customersStressLevel_variance = customersStressLevel_variance;
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