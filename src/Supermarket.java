import sim.engine.*;
import sim.field.grid.*;

import java.util.ArrayList;

public class Supermarket extends SimState {
	// set locations for checkout or spawn point of the customers
	private static final int CUSTOMER_X = 100;
	private static final int CUSTOMER_Y = 75;

	public static final int CHECKOUT_X = 25;
	public static final int CHECKOUT_Y = 75;

	private static final int CUSTOMER = 1;
	private static final int CHECKOUT = 2;

	// some properties
	private int numCustomers = 100;
	private int checkoutCustomersAmount_variance = 4;

	public IntGrid2D sites;
	public DoubleGrid2D toCheckoutGrid;
	public SparseGrid2D supermarketGrid;

	private static final long serialVersionUID = 1;

	private static final int GRID_HEIGHT = 101;
	private static final int GRID_WIDTH = 101;

	private static int xPosOfLastInLine = CHECKOUT_X;
	private static int yPosOfLastInLine = CHECKOUT_Y;

	private static ArrayList<Customer> checkoutQueue = new ArrayList<>();

	public Supermarket(long seed) {
		super(seed);
	}

	public void start() {
		super.start();  // clear out the schedule

		// make new grids
		sites = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);
		toCheckoutGrid = new DoubleGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);
		supermarketGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);

		sites.field[CUSTOMER_X][CUSTOMER_Y] = CUSTOMER;
		sites.field[CHECKOUT_X][CHECKOUT_Y] = CHECKOUT;

		for (int x = 0; x < numCustomers; x++) {
			Customer customer = new Customer();
			supermarketGrid.setObjectLocation(customer, CUSTOMER_X, CUSTOMER_Y);
			int delay = delayMovement();
			schedule.scheduleRepeating(Schedule.EPOCH + delay /* The customer spawns at the given time*/, 0, customer, 1 /* Perform an act at each 'interval' steps */);
		}

		// Schedule evaporation to happen after the customers move and update
		schedule.scheduleRepeating(Schedule.EPOCH, 1, new Steppable() {
			public void step(SimState state) {
				toCheckoutGrid.multiply(1);
			}
		}, 1);

	}

	private int delayMovement() {
		return (int) (Math.random() * 1000);
		// TODO this spawn has to be delayed by a normal deviation
	}

	public static synchronized void addCustomerToCheckoutQueue(Customer customer) {
		checkoutQueue.add(customer);
	}

	public static synchronized void removeFirstCustomerFromCheckoutQueue() {
		checkoutQueue.remove(0); // TODO we may want to check whether we deleted the correct object
	}

	public static synchronized long currentLineTotalDuration() {
		long totalDelay = 0;
		for(Customer customer : checkoutQueue) {
			totalDelay += customer.getOwnCheckoutDuration();
		}
		return totalDelay;
	}

	// getter and setter for the model
	public int getCheckoutCustomersAmount_variance() {
		return checkoutCustomersAmount_variance;
	}

	public void setCheckoutCustomersAmount_variance(int checkoutCustomersAmount_variance) {
		this.checkoutCustomersAmount_variance = checkoutCustomersAmount_variance;
	}

	public int getCheckoutProcessingTime_variance() {
		return checkoutProcessingTime_variance;
	}

	public void setCheckoutProcessingTime_variance(int checkoutProcessingTime_variance) {
		this.checkoutProcessingTime_variance = checkoutProcessingTime_variance;
	}

	private int checkoutProcessingTime_variance = 5;

	public int getNumCustomers() {
		return numCustomers;
	}

	public void setNumCustomers(int val) {
		if (val > 0) numCustomers = val;
	}
}





