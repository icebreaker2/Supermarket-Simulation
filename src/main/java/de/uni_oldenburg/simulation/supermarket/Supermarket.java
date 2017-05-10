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

	private double customerInfirm_probability = 0.1;
	private double customerStressed_probability = 0.2;
	private double customerPrefersCheckoutOne_probability = 0.25;

	private int totalCustomersAmount = 0;

	public IntGrid2D supermarketMap;
	public SparseGrid2D customerGrid;

	public static final int GRID_HEIGHT = 50;
	public static final int GRID_WIDTH = 4;

	Customer[] customersAtCheckout = new Customer[GRID_WIDTH]; // Just a reference

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

			Customer[] newCustomers = new Customer[GRID_WIDTH];

			// Customers may arrive at any point
			for (int i = 0; i < GRID_WIDTH; i++) {
				// Generate new customers randomly
				if (newCustomerArrived()) {
					newCustomers[i] = new Customer(this);
				}
			}

			// Set customers into supermarket
			for (int i = 0; i < GRID_WIDTH; i++) {
				if (newCustomers[i] != null) {
					customerGrid.setObjectLocation(newCustomers[i], i, SPAWN_POSITION_Y);
					schedule.scheduleRepeating(newCustomers[i], 1);
					totalCustomersAmount++;
				}
			}
		}, 1);

		// Checkout customers at the checkout
		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {

			// New customer arrived at checkout
			for (int i = 0; i < GRID_WIDTH; i++) {
				if (customerGrid.getObjectsAtLocation(i, CHECKOUT_POSITION_Y) != null) {
					Customer customer = (Customer) customerGrid.getObjectsAtLocation(i, CHECKOUT_POSITION_Y).get(0);
					if (customer != customersAtCheckout[i]) {
						schedule.scheduleOnceIn(customer.getCheckoutTime(), (Steppable) (SimState leaveState) -> {
							customerGrid.remove(customer);
						});
						customersAtCheckout[i] = customer;
					}
				}
			}
		}, 1);
	}

	/**
	 * Randomly decides if a new customer arrived.
	 * Adds a drift to gain given mean and variance.
	 *
	 * @return new customer to be added
	 */
	private boolean newCustomerArrived() {
		return (checkoutCustomersAmount_mean*GRID_WIDTH - checkoutCustomersAmount_variance)  > random.nextGaussian() * checkoutCustomersAmount_variance + getNumberOfWaitingCustomers();
	}

	/**
	 * @return Probability if a new customer is infirm
	 */
	public double getCustomerInfirm_probability() {
		return customerInfirm_probability;
	}

	/**
	 * @param customerInfirm_probability Probability if a new customer is infirm
	 */
	public void setCustomerInfirm_probability(double customerInfirm_probability) {
		this.customerInfirm_probability = customerInfirm_probability;
	}

	/**
	 * @return Probability if a new customer is stressed
	 */
	public double getCustomerStressed_probability() {
		return customerStressed_probability;
	}

	/**
	 * @param customerStressed_probability Probability if a new customer is stressed
	 */
	public void setCustomerStressed_probability(double customerStressed_probability) {
		this.customerStressed_probability = customerStressed_probability;
	}

	/**
	 * @return Probability if a new customer prefers checkout one (e.g. in love)
	 */
	public double getCustomerPrefersCheckoutOne_probability() {
		return customerPrefersCheckoutOne_probability;
	}

	/**
	 * @param customerPrefersCheckoutOne_probability Probability if a new customer prefers checkout one (e.g. in love)
	 */
	public void setCustomerPrefersCheckoutOne_probability(double customerPrefersCheckoutOne_probability) {
		this.customerPrefersCheckoutOne_probability = customerPrefersCheckoutOne_probability;
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
		int waitingCustomers = 0;
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				try {
					if (customerGrid != null & customerGrid.getObjectsAtLocation(x, y) != null) waitingCustomers++;
				} catch (NullPointerException e) {}
			}
		}
		return waitingCustomers;
	}

	/**
	 * @return The total number of customers since simulation start
	 */
	public int getTotalCustomersAmount() {
		return totalCustomersAmount;
	}
}