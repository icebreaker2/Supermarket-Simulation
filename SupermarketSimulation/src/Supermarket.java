import sim.engine.*;
import sim.field.grid.*;

import java.util.ArrayList;

public class Supermarket extends SimState {
	// set locations for checkstand or spawn point of the customers
	public static final int CUSTOMER_X = 100;
	public static final int CUSTOMER_Y = 75;

	public static final int CHECKSTAND_X = 25;
	public static final int CHECKSTAND_Y = 75;

	public static final int CUSTOMER = 1;
	public static final int CHECKSTAND = 2;

	// some properties
	public int numCustomers = 100;
	public int checkstandCustomersAmount_variance = 4;

	public IntGrid2D sites;
	public DoubleGrid2D toCheckstandGrid;
	public SparseGrid2D supermarketgrid;

	private static final long serialVersionUID = 1;

	private static final int GRID_HEIGHT = 101;
	private static final int GRID_WIDTH = 101;

	private static ArrayList<Customer> checkstandQueue = new ArrayList<>();

	public Supermarket(long seed) {
		super(seed);
	}

	public void start() {
		super.start();  // clear out the schedule

		// make new grids
		sites = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);
		toCheckstandGrid = new DoubleGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);
		supermarketgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);

		sites.field[CUSTOMER_X][CUSTOMER_Y] = CUSTOMER;
		sites.field[CHECKSTAND_X][CHECKSTAND_Y] = CHECKSTAND;

		for (int x = 0; x < numCustomers; x++) {
			Customer customer = new Customer();
			supermarketgrid.setObjectLocation(customer, CUSTOMER_X, CUSTOMER_Y);
			int delay = delayMovement();
			schedule.scheduleRepeating(Schedule.EPOCH + delay /* The customer spawns at the given time*/, 0, customer, 1 /* Perform a an act at each 'interval' steps */);
		}

		// Schedule evaporation to happen after the customers move and update
		schedule.scheduleRepeating(Schedule.EPOCH, 1, new Steppable() {
			public void step(SimState state) {
				toCheckstandGrid.multiply(1);
			}
		}, 1);

	}

	private int delayMovement() {
		return (int) (Math.random() * 1000);
		// TODO this spawn has to be delayed by a normal deviation
	}

	public static synchronized void addCustomerToCheckstandQueue (Customer customer) {
		checkstandQueue.add(customer);
	}

	public static synchronized void removeFirstCustomerFromCheckstandQueue () {
		checkstandQueue.remove(0); // TODO we may want to check whether we deleted the correct object
	}

	public static synchronized long computeDelayFromTheTop() {
		long totalDelay = 0;
		for(Customer customer : checkstandQueue) {
			totalDelay += customer.getProcessingDelay();
		}
		return totalDelay;
	}

	// getter and setter for the model
	public int getCheckstandCustomersAmount_variance() {
		return checkstandCustomersAmount_variance;
	}

	public void setCheckstandCustomersAmount_variance(int checkstandCustomersAmount_variance) {
		this.checkstandCustomersAmount_variance = checkstandCustomersAmount_variance;
	}

	public int getCheckstandProcessingTime_variance() {
		return checkstandProcessingTime_variance;
	}

	public void setCheckstandProcessingTime_variance(int checkstandProcessingTime_variance) {
		this.checkstandProcessingTime_variance = checkstandProcessingTime_variance;
	}

	public int checkstandProcessingTime_variance = 5;

	public int getNumCustomers() {
		return numCustomers;
	}

	public void setNumCustomers(int val) {
		if (val > 0) numCustomers = val;
	}
}




