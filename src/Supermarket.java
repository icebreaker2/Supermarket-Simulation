import ec.util.MersenneTwisterFast;
import sim.engine.*;
import sim.field.grid.*;

public class Supermarket extends SimState {

	// set locations for checkout and spawn point of the customers
	public static final int SPAWN_POSITION = 0;
	public static final int CHECKOUT_POSITION = 49;

	// Sites
	public static final int SPAWN_POINT = 1;
	public static final int CHECKOUT_POINT = 2;

	// some initial properties
	private int numCustomers = 1440;
	private int checkoutCustomersAmount_variance = 4;
	private int checkoutProcessingTime_variance = 5;

	public IntGrid2D sites;
	public SparseGrid2D supermarketGrid;

	public static final int GRID_HEIGHT = 50;
	public static final int GRID_WIDTH = 1;

	MersenneTwisterFast checkOutTimeGenerator = new MersenneTwisterFast();
	Customer customerAtCheckout; // Just a reference

	public Supermarket(long seed) {
		super(seed);
	}

	public void start() {
		super.start();  // clear out the schedule

		// Make new grids
		sites = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);
		supermarketGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);

		// Set sites
		sites.field[0][SPAWN_POSITION] = SPAWN_POINT;
		sites.field[0][CHECKOUT_POSITION] = CHECKOUT_POINT;

		// Position the customers
		for (int x = 0; x < numCustomers; x++) {
			Customer customer = new Customer();
			supermarketGrid.setObjectLocation(customer, 0, SPAWN_POSITION);

			// The customer spawns at the given time
			schedule.scheduleRepeating(Schedule.EPOCH + delayCustomerStart(), 0, customer, 1);
		}

		// Schedule evaporation to happen after the customers move and update
		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) state -> {
			if (supermarketGrid.getObjectsAtLocation(0, CHECKOUT_POSITION) != null) {
				Customer customer = (Customer) supermarketGrid.getObjectsAtLocation(0, CHECKOUT_POSITION).get(0);
				if (customer != customerAtCheckout) {
					customer.startCheckOut(getCheckoutTime());
					customerAtCheckout = customer;
				}
			}
		}, 20);
	}

	private int delayCustomerStart() {
		return (int) (Math.random() * 43200); // 60*60*12 = 43200 opening seconds per day
	}

	private int getCheckoutTime() {
		return (int) Math.round(checkOutTimeGenerator.nextGaussian()*5.0+30.0);
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

	public int getNumCustomers() {
		return numCustomers;
	}

	public void setNumCustomers(int val) {
		if (val > 0) numCustomers = val;
	}
}