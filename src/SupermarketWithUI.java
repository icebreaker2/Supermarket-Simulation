import sim.engine.*;
import sim.display.*;
import sim.portrayal.grid.*;
import sim.util.gui.SimpleColorMap;

import java.awt.*;
import javax.swing.*;

public class SupermarketWithUI extends GUIState {

	private Display2D display;
	private JFrame displayFrame;

	private FastValueGridPortrayal2D sitesPortrayal = new FastValueGridPortrayal2D("Sites", false);
	private SparseGridPortrayal2D customersPortrayal = new SparseGridPortrayal2D();

	public static void main(String[] args) {
		new SupermarketWithUI().createController();
	}

	public SupermarketWithUI() {
		super(new Supermarket(System.currentTimeMillis()));
	}

	public SupermarketWithUI(SimState state) {
		super(state);
	}

	// allow the user to inspect the model
	public Object getSimulationInspectedObject() {
		return state;
	}

	public static String getName() {
		return "Supermarket Simulation";
	}

	private void setupPortrayals() {

		Supermarket supermarket = (Supermarket) state;

		// tell the portrayals what to portray and how to portray them
		customersPortrayal.setField(supermarket.customerGrid);
		sitesPortrayal.setField(supermarket.supermarketMap);

		// Set colors for supermarketMap
		Color[] colorMap = new Color[3];
		colorMap[0] = new Color(0, 0, 0, 0);
		colorMap[supermarket.SPAWN_POINT_ID] = new Color(0, 255, 0, 255);
		colorMap[supermarket.CHECKOUT_POINT_ID] = new Color(255, 0, 0, 255);
		sitesPortrayal.setMap(new SimpleColorMap(colorMap));

		// reschedule the displayer
		display.reset();

		// redraw the display
		display.repaint();
	}

	public void start() {
		super.start();  // set up everything but replacing the display
		// set up our portrayals
		setupPortrayals();
	}

	public void load(SimState state) {
		super.load(state);
		// we now have new grids. Set up the portrayals to reflect that
		setupPortrayals();
	}

	public void init(Controller c) {
		super.init(c);

		// Make the Display2D. We'll have it display stuff later.
		display = new Display2D(10, 510, this); // At 10x510, we've got 10x10 per array position
		displayFrame = display.createFrame();
		c.registerFrame(displayFrame);   // Register the frame so it appears in the "Display" list
		displayFrame.setVisible(true);

		// attach the portrayals from bottom to top
		display.attach(sitesPortrayal, "Site Locations");
		display.attach(customersPortrayal, "Customers");

		// specify the backdrop color  -- what gets painted behind the displays
		display.setBackdrop(Color.gray);
	}

	public void quit() {
		super.quit();

		// disposing the displayFrame automatically calls quit() on the display,
		// so we don't need to do so ourselves here.
		if (displayFrame != null) displayFrame.dispose();
		displayFrame = null;  // let gc
		display = null;       // let gc
	}
}