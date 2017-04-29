import sim.engine.*;
import sim.field.grid.*;
import sim.util.*;


public class Supermarket extends SimState {
	private static final long serialVersionUID = 1;

	public static final int GRID_HEIGHT = 100;
	public static final int GRID_WIDTH = 100;

	// set locations for checkstand or spawn point of the customers
	public static final int Customer_XMIN = 75;
	public static final int Customer_XMAX = 75;
	public static final int Customer_YMIN = 75;
	public static final int Customer_YMAX = 75;

	public static final int Checkstand_XMIN = 25;
	public static final int Checkstand_XMAX = 25;
	public static final int Checkstand_YMIN = 25;
	public static final int Checkstand_YMAX = 25;

	public static final int CUSTOMER = 1;
	public static final int CHECKSTAND = 2;

	// some properties
	public int numCustomers = 1000;
	public int checkstandCustomersAmount_variance = 4;

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

	public IntGrid2D sites;
	public DoubleGrid2D toCheckstandGrid;
	public SparseGrid2D supermarketgrid;

	public Supermarket(long seed) {
		super(seed);
	}

	public void start() {
		super.start();  // clear out the schedule

		// make new grids
		sites = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);
		toCheckstandGrid = new DoubleGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);
		supermarketgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);

		// initialize the grid with the spawn point of the customers and the checkstand sites
		for (int x = Customer_XMIN; x <= Customer_XMAX; x++)
			for (int y = Customer_YMIN; y <= Customer_YMAX; y++)
				sites.field[x][y] = CUSTOMER;
		for (int x = Checkstand_XMIN; x <= Checkstand_XMAX; x++)
			for (int y = Checkstand_YMIN; y <= Checkstand_YMAX; y++)
				sites.field[x][y] = CHECKSTAND;

		for (int x = 0; x < numCustomers; x++) {
			Customer customer = new Customer();
			supermarketgrid.setObjectLocation(customer, (Customer_XMAX + Customer_XMIN) / 2, (Customer_YMAX + Customer_YMIN) / 2);
			schedule.scheduleRepeating(Schedule.EPOCH + x, 0, customer, 1);
		}

		// Schedule evaporation to happen after the customers move and update
		schedule.scheduleRepeating(Schedule.EPOCH, 1, new Steppable() {
			public void step(SimState state) {
				toCheckstandGrid.multiply(1);
			}
		}, 1);

	}

	public static void main(String[] args) {
		doLoop(Supermarket.class, args);
		System.exit(0);
	}
}





