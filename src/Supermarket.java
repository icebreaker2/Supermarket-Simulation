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
	private double checkoutCustomersAmount_mean = 12.0;
	private double checkoutCustomersAmount_variance = 4.0;
	private double checkoutProcessingTime_mean = 30.0;
	private double checkoutProcessingTime_variance = 5.0;
	private int totalCustomersAmount = 0;

	public IntGrid2D sites;
	public SparseGrid2D supermarketGrid;

	public static final int GRID_HEIGHT = 50;
	public static final int GRID_WIDTH = 1;

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

		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {

			// Add new Customers
			if (newCustomerArrived()) {
				Customer customer = new Customer();
				totalCustomersAmount++;
				supermarketGrid.setObjectLocation(customer, 0, SPAWN_POSITION);
				schedule.scheduleRepeating(customer, 1);
			}
		}, Math.round(Math.sqrt(checkoutProcessingTime_mean)));

		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {


			// Start checkout of customers
			if (supermarketGrid.getObjectsAtLocation(0, CHECKOUT_POSITION) != null) {
				Customer customer = (Customer) supermarketGrid.getObjectsAtLocation(0, CHECKOUT_POSITION).get(0);
				if (customer != customerAtCheckout) {
					schedule.scheduleOnceIn(getCheckoutTime(), (Steppable) (SimState leaveState) -> {
						supermarketGrid.remove(customer);
					});
					customerAtCheckout = customer;
				}
			}
		}, 1);
	}

	// TODO: This is not working properly. Variance always adds to the mean and does'nt scale.
	private boolean newCustomerArrived() {
		if (checkoutCustomersAmount_mean-checkoutCustomersAmount_variance > random.nextGaussian()*checkoutCustomersAmount_variance+getNumberOfWaitingCustomers()) {
			return true;
		} else {
			return false;
		}
	}

	private int getCheckoutTime() {
		int checkoutTime = (int) Math.round(random.nextGaussian()*checkoutProcessingTime_variance+checkoutProcessingTime_mean);

		// Checkout time must be positive
		if (checkoutTime > 0) {
			return checkoutTime;
		} else {
			return 1;
		}
	}

	// getter and setter for the model
	public double getCheckoutCustomersAmount_variance() {
		return checkoutCustomersAmount_variance;
	}

	public void setCheckoutCustomersAmount_variance(double checkoutCustomersAmount_variance) {
		this.checkoutCustomersAmount_variance = checkoutCustomersAmount_variance;
	}

	public double getCheckoutCustomersAmount_mean() {
		return checkoutCustomersAmount_mean;
	}

	public void setCheckoutCustomersAmount_mean(double checkoutCustomersAmount_mean) {
		this.checkoutCustomersAmount_mean = checkoutCustomersAmount_mean;
	}

	public double getCheckoutProcessingTime_variance() {
		return checkoutProcessingTime_variance;
	}

	public void setCheckoutProcessingTime_variance(double checkoutProcessingTime_variance) {
		this.checkoutProcessingTime_variance = checkoutProcessingTime_variance;
	}

	public double getCheckoutProcessingTime_mean() {
		return checkoutProcessingTime_mean;
	}

	public void setCheckoutProcessingTime_mean(double checkoutProcessingTime_mean) {
		this.checkoutProcessingTime_mean = checkoutProcessingTime_mean;
	}

	public int getNumberOfWaitingCustomers() {
		if (supermarketGrid != null) {
			return supermarketGrid.getAllObjects().size();
		} else {
			return 0;
		}
	}

	public int getTotalCustomersAmount() {
		return totalCustomersAmount;
	}
}